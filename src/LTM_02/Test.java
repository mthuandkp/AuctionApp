/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LTM_02;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author ADMIN
 */
public class Test {
    public static void main(String[] args) {
        System.out.println(LocalTime.now());
        while(true){
            
            System.out.println(LocalTime.parse("18:21:01.015").until( LocalTime.now(), ChronoUnit.SECONDS));
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
        }
    }
}
