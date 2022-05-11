/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LTM_02;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

/**
 *
 * @author ADMIN
 */
public class Server {

    ServerSocket ss;
    List<User> listUser = new ArrayList<>();
    List<Product> listProduct = new ArrayList<>();
    List<Socket> listClient = new ArrayList<>();
    Product auctionProduct = null;
    LocalTime startAuction = null;
    ConnectDB conn = new ConnectDB();
    int maxPrice = 0;
    int maxUser = 0;

    public Server() {
        try {
            System.out.println("Server starting....");
            ss = new ServerSocket(4444);

            new Thread(new DauGiaSv()).start();
            while (true) {
                Socket s = ss.accept();
                System.out.println("Client connected");

                new Thread(new ReadServer(s)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Server();
    }

    private class DauGiaSv implements Runnable {

        public DauGiaSv() {
        }

        @Override
        public void run() {
            while (true) {
                if (listClient != null && listClient.isEmpty() == false) {
                    if (startAuction == null || startAuction.until(LocalTime.now(), ChronoUnit.SECONDS) > 60) {
                        rejectAllUser();

                        sendRequest();
                    } else {

                    }
                }
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void sendRequest() {
            try {
                //Gui thong bao den tat ca client
                auctionProduct = conn.getAuctionProduct();

                for (Socket s : listClient) {
                    BufferedWriter outS = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                    JSONObject js = new JSONObject();
                    js.put("req", "CONFIRM_ACCEPT");
                    js.put("sms", "sản phẩm " + auctionProduct.getName() + " đang đấu giá với mức khởi đầu " + auctionProduct.getStartPrice() + " bạn có muốn tham gia ?");

                    System.out.println("Server sent " + js.toString());
                    outS.write(js.toString());
                    outS.newLine();
                    outS.flush();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            startAuction = LocalTime.now();
        }

        private void rejectAllUser() {
            sendResult();
            try {
                if (listClient == null) {
                    return;
                }
                for (Socket s : listClient) {
                    BufferedWriter outS = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                    JSONObject js = new JSONObject();
                    js.put("req", "CLOSE");

                    outS.write(js.toString());
                    outS.newLine();
                    outS.flush();
                }
                startAuction = null;

                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void sendResult() {
            try {

                if (startAuction != null && startAuction.until(LocalTime.now(), ChronoUnit.SECONDS) > 60 && maxUser == 0) {
                    System.out.println("no partition client");
                }

                User user = conn.getUserById(maxUser);
                if (user == null) {
                    for (Socket s : listClient) {
                        BufferedWriter outS = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                        JSONObject js = new JSONObject();
                        js.put("req", "RESULT");
                        js.put("sms", "Đấu giá thất bại do chưa ai tham gia");

                        outS.write(js.toString());
                        outS.newLine();
                        outS.flush();
                    }
                    return;
                }
                for (Socket s : listClient) {
                    BufferedWriter outS = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                    JSONObject js = new JSONObject();
                    js.put("req", "RESULT");
                    js.put("sms", "Người dùng\"" + user.getUname() + "\" đã thắng phiên đấu giá với sô tiền :" + maxPrice);

                    outS.write(js.toString());
                    outS.newLine();
                    outS.flush();
                }

                conn.subtractMoney(maxUser, maxPrice);
                System.out.println(auctionProduct);
                if (auctionProduct != null) {
                    System.out.println(maxUser);
                    
                    conn.buyProduct(maxUser, auctionProduct.getId());
                }
                maxPrice = 0;
                maxUser = 0;

                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class ReadServer implements Runnable {

        Socket socket;
        BufferedReader in;
        BufferedWriter out;

        public ReadServer(Socket s) {
            this.socket = s;
            try {
                in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public synchronized void run() {
            try {

                while (true) {
                    String clientInput = in.readLine();
                    if (clientInput == null) {
                        break;
                    }

                    JSONObject js = new JSONObject(clientInput);

                    String result = process(js);
                    if (result.equals("")) {
                        continue;
                    }

                    out.write(result);
                    out.newLine();
                    out.flush();
                }
                in.close();
                out.close();

                for (Socket s : listClient) {
                    if (s == socket) {
                        listClient.remove(s);
                        break;
                    }
                }
                System.out.println("Client disconnected");

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        private String process(JSONObject js) {
            String req = js.getString("req");
            JSONObject rJs = new JSONObject();
            switch (req) {
                case "LOGIN": {
                    String uname = js.getString("uname");
                    String pass = js.getString("pass");
                    rJs.put("req", "LOGIN");

                    User loginuser = conn.login(uname, pass);
                    if (loginuser == null) {
                        rJs.put("rs", "FAIL");
                        return rJs.toString();
                    } else {
                        if (loginuser.isLock()) {
                            rJs.put("rs", "BLOCK");
                            return rJs.toString();
                        } else {
                            rJs.put("rs", "SUCCESS");
                            rJs.put("id", loginuser.getId());

                            listClient.add(socket);

                            if (startAuction != null && startAuction.until(LocalTime.now(), ChronoUnit.SECONDS) < 60) {
                                js = new JSONObject();
                                js.put("req", "CONFIRM_ACCEPT");
                                js.put("sms", "sản phẩm " + auctionProduct.getName() + " đang đấu giá với mức khởi đầu " + auctionProduct.getStartPrice() + " bạn có muốn tham gia ?");

                                try {
                                    out.write(js.toString());
                                    out.newLine();
                                    out.flush();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                            return rJs.toString();
                        }
                    }
                }
                case "ACCEPT": {
                    rJs = new JSONObject();
                    rJs.put("req", "ACCEPT");
                    if (auctionProduct != null) {
                        rJs.put("idP", auctionProduct.getId());
                        rJs.put("nameP", auctionProduct.getName());
                        rJs.put("startPrice", auctionProduct.getStartPrice());
                        rJs.put("image", auctionProduct.getImage());
                    }
                    if (startAuction.until(LocalTime.now(), ChronoUnit.SECONDS) <= 0) {
                        rJs.put("sms", "FAIL");
                    } else {
                        rJs.put("sms", "SUCCESS");
                    }

                    return rJs.toString();

                }
                case "BID": {
                    int value = js.getInt("value");
                    int userId = js.getInt("userid");
                    rJs = new JSONObject();
                    rJs.put("req", "BID");

                    User user = conn.getUserById(userId);
                    if (value > user.getBalance()) {
                        rJs.put("sms", "OVER_BALANCE");
                        rJs.put("max", user.getBalance());
                    } else {
                        if (value > maxPrice) {
                            maxPrice = value;
                            maxUser = userId;
                            conn.lockUser(userId);

                        }
                        rJs.put("sms", "SUCCESS");
                    }

                    return rJs.toString();
                }
                default: {
                    return "";
                }
            }
        }
    }

}
