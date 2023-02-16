/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Project1;

import java.io.*;
import java.util.*;

/**
 *
 * @author korn
 */
public class Project1 {
    public static void main(String args[]){
        
    Product P[] = new Product[5];
    String path = "src/main/java/Project1_6480929/";
    FileHandler FH = new FileHandler(path, "product.txt");
    FH.wrongProductFile_loop(P);
    
    }
}

class Product{
    private String productName;
    private int productPrice, productWeight, totalUnits, totalSales;
    
    public Product(String n, int p, int w){
        this.productName = n;
        this.productPrice = p;
        this.productWeight = w;
        this.totalSales = 0;
        this.totalUnits = 0;
    }
    
    public int calculateTotalSales(int units){
        int totalSales = units * productPrice;
        return totalSales;
    }
}

class Customer{
    private String name;
    private int cashBacks;
    
    public Customer(String n){
        this.name = n;
        this.cashBacks = 0;
    }
    
    public int cashBackRedemtion(int totalBills){
        int MaxCashBacks = Math.min(cashBacks, 100);
        int redemption = Math.min(MaxCashBacks, totalBills);
        cashBacks -= redemption;
        return redemption;
    }
    
    public void addCashBacks(int totalPrices){
        double earnCashBack = Math.floor(totalPrices * 0.01);
        cashBacks += earnCashBack;
        int cashBacksInt = (int) cashBacks;  //get rid of demimal
    }
}

class Order{

}

//Feel free to make duplicate method in this class for file handling and caught Exception
//Every file handling method will be put in this class!!
class FileHandler{
    private final String path;
    private String fileName;
    private final Scanner keyboardScan;
    
    public FileHandler(String p, String fn){
        path = p;
        fileName = fn;
        keyboardScan = new Scanner(System.in);
    }
    
    public void productFileProcessLine(String line, Product[] P, int i){
        try{
            String [] buf = line.split(",");
            
            String productName = buf[0].trim();
            int productPrice = Integer.parseInt(buf[1].trim());
            int productWeight = Integer.parseInt(buf[2].trim());
            
            P[i] = new Product(productName, productPrice, productWeight);
        }catch(RuntimeException e){   
            System.out.println(e);
            System.out.println(line + "\n");
        }
    }
    
    public void wrongProductFile_loop(Product[] P){
        boolean opensuccess = false;
        int i = 0;
        while (!opensuccess){
            try(Scanner fileScan = new Scanner(new File(path + fileName))){
                opensuccess = true;
                while(fileScan.hasNext() && i < 5){
                    productFileProcessLine(fileScan.nextLine(), P, i);
                    i++;
                }
            }catch(FileNotFoundException e){
                System.out.println(e);
                System.out.println("New file name = ");
                fileName = keyboardScan.next();
                System.out.print("\n");
            }
        }
    }
}
