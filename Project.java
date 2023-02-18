/*
Possathorn Sujipisut 6480274
Phakkhapon Kaewmanee 6480929
 */
package Project_6480929;

import java.io.*;
import java.util.*;

/**
 *
 * @author korn
 */
public class Project {

    public static void main(String args[]) {

        Product P[] = new Product[5];
        String path = "src/main/java/Project_6480929/";
        FileHandler FH = new FileHandler(path, "products.txt");
        FH.wrongProductFile_loop(P);

        //GET RID ONCE HAVE SHIPPING
        //Handling Orders Errors
        ArrayList<Order> orders = new ArrayList<Order>();
        FileHandler OFH = new FileHandler(path, "orders.txt");

        //Adding each new customer
        ArrayList<Customer> c = new ArrayList<Customer>();
        OFH.wrongOrderFile_loop(orders, c);

        ArrayList<ShippingCalculator> shipping = new ArrayList<ShippingCalculator>();
        FileHandler SHF = new FileHandler(path, "shipping.txt");
        SHF.wrongShippingFile_loop(shipping);

        //Order processing and printing
        System.out.printf("\n==== Order Processing ====");
        for (Order o : orders) {
            o.orderProcessing(P, c, shipping);
        }

    }
}

class Product {

    private String productName;
    private int productPrice, productWeight, totalUnits, totalSales;

    public Product(String n, int p, int w) {
        this.productName = n;
        this.productPrice = p;
        this.productWeight = w;
        this.totalSales = 0;
        this.totalUnits = 0;
    }

    public int returnPrice() {
        return productPrice;
    }

    public int returnWeight() {
        return productWeight;
    }

    public String returnName() {
        return productName;
    }

    public int calculateTotalSales(int units) {
        int totalSales = units * productPrice;
        return totalSales;
    }
}

class Customer {

    private String name;
    private int cashBacks;

    public Customer(String n) {
        this.name = n;
        this.cashBacks = 0;
    }

    public String returnName() {
        return name;
    }

    public int cashBackRedemtion(int totalBills) {
        int MaxCashBacks = Math.min(cashBacks, 100);
        int redemption = Math.min(MaxCashBacks, totalBills);
        cashBacks -= redemption;
        return redemption;
    }

    public void addCashBacks(int totalPrices) {
        double earnCashBack = Math.floor(totalPrices * 0.01);
        cashBacks += earnCashBack;
        int cashBacksInt = (int) cashBacks;  //get rid of demimal
    }

    public void print() {
        System.out.println(name);
    }

    public int returnCashback() {
        return cashBacks;
    }

}

class Order {

    //Order number and name
    private int orderNumber;
    private String orderName;

    //Number of each item
    private int item1, item2, item3, item4, item5;

    //Shipping type
    private String shipping, shippingType;
    //shipping is for reading from txt, shippingType is for String name
    private int shippingId, shippingPrice; //Standard = 0, Express = 1

    //Total price and weight
    private int totalPrice, totalWeight, finalBill;

    private int getShippingPrice() {
        return shippingPrice;
    }

    public Order(int orderNum, String n, String s, int i1, int i2, int i3,
            int i4, int i5, ArrayList<Customer> c) {
        orderNumber = orderNum;
        orderName = n;
        shipping = s;
        item1 = i1;
        item2 = i2;
        item3 = i3;
        item4 = i4;
        item5 = i5;

        //Check if customer already exists and if not add them
        //First Customer add
        if (orderNumber == 1) {
            Customer firstCustomer = new Customer(orderName);
            c.add(firstCustomer);
        }

        //If for loop detects a same name ONCE it just not add the customer
        boolean costomerCheck = true;
        for (int i = 0; i < c.size(); i++) {
            Customer check = c.get(i);
            if (orderName.equals(check.returnName())) {
                costomerCheck = false;
            }
        }
        if (costomerCheck == true) {
            Customer addCustomer = new Customer(orderName);
            c.add(addCustomer);
        }
    }

    public void orderProcessing(Product p[], ArrayList<Customer> c, ArrayList<ShippingCalculator> shippingcal) {
        totalPrice = 0;
        totalWeight = 0;

        //Calcualte total price and weight
        totalPrice = p[0].returnPrice() * item1;
        totalWeight = p[0].returnWeight() * item1;

        totalPrice = totalPrice + (p[1].returnPrice() * item2);
        totalWeight = totalWeight + (p[1].returnWeight() * item2);

        totalPrice = totalPrice + (p[2].returnPrice() * item3);
        totalWeight = totalWeight + (p[2].returnWeight() * item3);

        totalPrice = totalPrice + (p[3].returnPrice() * item4);
        totalWeight = totalWeight + (p[3].returnWeight() * item4);

        totalPrice = totalPrice + (p[4].returnPrice() * item5);
        totalWeight = totalWeight + (p[4].returnWeight() * item5);

        //Cashback System
        boolean costomerFind = false;
        Customer find = c.get(0);
        for (int i = 0; i < c.size(); i++) {
            if (costomerFind == false) {
                find = c.get(i);
            }
            if (orderName.equals(find.returnName())) {
                costomerFind = true;
            }
        }
        int cashback = find.cashBackRedemtion(totalPrice);
        find.addCashBacks(totalPrice);
        int futureCashback = find.cashBackRedemtion(totalPrice);
        totalPrice = totalPrice - cashback;

        //CHANGE FOR SHIPPING
        //CHANGE FOR SHIPPING
        if (shipping.equalsIgnoreCase("S")) {
            shippingId = 0;
            shippingType = "(standard)";
        } else {
            shippingId = 1;
            shippingType = "(express)";
        }

        shippingPrice = ShippingCalculator.Calculate(shippingId, shippingType, totalWeight, shipping, shippingPrice, shippingcal);

        finalBill = totalPrice + shippingPrice;

        //Printing Everything
        //Order number, shipping type, orderer
        System.out.printf("\nOrder %2d%11s,%6s >> ",
                orderNumber, shippingType, orderName);

        //Item types and quainty
        System.out.printf("%17s(%2d)%17s(%2d)%17s(%2d)%17s(%2d)%17s(%2d)",
                p[0].returnName(), item1, p[1].returnName(), item2,
                p[2].returnName(), item3, p[3].returnName(), item4,
                p[4].returnName(), item5);

        //Cashback
        System.out.printf("\n%40s%6s", ">> Available cashback = ",
                String.format("%,d", cashback));

        //Total price
        System.out.printf("\n%40s%6s", ">> Total price = ",
                String.format("%,d", totalPrice));

        //Total weight
        System.out.printf("\n%40s%6s%15s%s", ">> Total weight = ",
                String.format("%,d", totalWeight), "Shipping Fee = ",
                String.format("%,d", shippingPrice));

        //Final Bill
        System.out.printf("\n%40s%6s%20s%s\n", ">> Final bill = ",
                String.format("%,d", finalBill), "Cashback for next order = ",
                String.format("%,d", futureCashback));
    }

    public int returntotalWeight() {
        return totalWeight;
    }
}

class ShippingCalculator implements Comparable<ShippingCalculator> {

    // Private instance variables to store shipping type, fee type, weight, and fee amounts
    private String shipping_type;
    private String fee_type;
    private int emin_weight;
    private int emax_weight;
    private int efee;
    private int smin_weight;
    private int smax_weight;
    private int sfee;
    private int surplus_threshold;
    private int weight;
    private int fee;

    // Override the compareTo method to sort ShippingCalculators by shipping type, fee type, emin_weight, and emax_weight
    @Override
    public int compareTo(ShippingCalculator other) {

        // Compare by shipping type, E first then S
        int shippingTypeComparison = this.getShippingType().compareTo(other.getShippingType());
        if (shippingTypeComparison != 0) {
            if (this.getShippingType().equals("E")) {
                return -1;
            } else if (this.getShippingType().equals("S") && other.getShippingType().equals("E")) {
                return 1;
            } else {
                return 0;
            }
        }

        // Compare by fee type, F first then V
        int feeTypeComparison = this.getFeeType().compareTo(other.getFeeType());
        if (feeTypeComparison != 0) {
            if (this.getFeeType().equals("F")) {
                return -1;
            } else {
                return 1;
            }
        }

        // Compare emin_weight, from the least to the most
        int eminWeightCmp = Integer.compare(this.emin_weight, other.emin_weight);
        if (eminWeightCmp != 0) {
            return eminWeightCmp;
        }

        // Compare emax_weight, from the least to the most
        return Integer.compare(this.emax_weight, other.emax_weight);
    }

    // Public getter methods for private instance variables
    private String getShippingType() {
        return shipping_type;
    }

    private String getFeeType() {
        return fee_type;
    }

    private int getExpressMinWeight() {
        return emin_weight;
    }

    private int getExpressMaxWeight() {
        return emax_weight;
    }

    private int getExpressFee() {
        return efee;
    }

    private int getStandardMinWeight() {
        return smin_weight;
    }

    private int getStandardMaxWeight() {
        return smax_weight;
    }

    private int getStandardFee() {
        return sfee;
    }

    private int getSurplusThreshold() {
        return surplus_threshold;
    }

    private int getWeight() {
        return weight;
    }

    private int getFee() {
        return fee;
    }

    // Constructor to create a new ShippingCalculator object
    public ShippingCalculator(String st, String ft, int miw, int maw, int f) {
        shipping_type = st;
        fee_type = ft;
        if (shipping_type.equalsIgnoreCase("E")) {
            emin_weight = miw;
            emax_weight = maw;
            efee = f;
        } else if (shipping_type.equalsIgnoreCase("S") && fee_type.equalsIgnoreCase("F")) {
            smin_weight = miw;
            smax_weight = maw;
            sfee = f;
        } else {
            surplus_threshold = miw;
            weight = maw;
            fee = f;
        }
    }

    private static int ShippingStandardCalculation(int totalWeight, ShippingCalculator sc3, ShippingCalculator sc4) {
        // Set initial values
        int totalWeightCounter = totalWeight;
        int shippingPrice = 0;

        // Check if total weight falls within standard shipping limits
        if (totalWeightCounter > sc3.getStandardMinWeight() && totalWeightCounter <= sc3.getStandardMaxWeight()) {
            shippingPrice += sc3.getStandardFee();  // If it does, add the standard shipping fee to the price
        } else {
            // Otherwise, add the standard shipping fee to the price and start calculating the additional weight surcharge
            shippingPrice += sc3.getStandardFee();
            totalWeightCounter -= sc4.getSurplusThreshold();
            int deduct = totalWeightCounter / sc4.getWeight();
            shippingPrice += (totalWeightCounter / sc4.getWeight()) * sc4.getFee();
            
            totalWeightCounter -= (deduct * sc4.getWeight());
            if (totalWeightCounter < sc4.getWeight() && totalWeightCounter != 0) {
                shippingPrice += sc4.getFee();
            }
            
        }

        return shippingPrice;

    }

    public static int Calculate(int shippingId, String shippingType, int totalWeight, String shipping, int shippingPrice, ArrayList<ShippingCalculator> shippingcal) {
        // Initialize array of ShippingCalculator objects
        ShippingCalculator[] sc = new ShippingCalculator[5];

        int totalWeightCounter = totalWeight;
        shippingPrice = 0;
        int i = 0;

        // Loop through shippingcal and add each ShippingCalculator object to sc array
        for (ShippingCalculator shippingCal : shippingcal) {
            sc[i] = shippingCal;
            ++i;
        }

        // Determine shipping type and calculate price accordingly
        if (shipping.equalsIgnoreCase("S")) {
            shippingId = 0;
            shippingType = "(standard)";
            shippingPrice = ShippingStandardCalculation(totalWeight, sc[3], sc[4]);  // If standard shipping, call ShippingStandardCalculation method
        } else {
            shippingId = 1;
            shippingType = "(express)";
            if (totalWeightCounter > sc[0].getExpressMinWeight() && totalWeightCounter <= sc[0].getExpressMaxWeight()) {
                shippingPrice = sc[0].getExpressFee();
            } else if (totalWeightCounter > sc[1].getExpressMinWeight() && totalWeightCounter <= sc[1].getExpressMaxWeight()) {
                shippingPrice = sc[1].getExpressFee();
            } else if (totalWeightCounter > sc[2].getExpressMinWeight() && totalWeightCounter <= sc[2].getExpressMaxWeight()) {
                shippingPrice = sc[2].getExpressFee();
            } else {
                shippingId = 0;
                shippingType = "(standard)";
                shippingPrice = ShippingStandardCalculation(totalWeight, sc[3], sc[4]);   // If none of the express shipping limits apply, call ShippingStandardCalculation method
            }
        }

        return shippingPrice;
    }

}

//Feel free to make duplicate method in this class for file handling and caught Exception
//Every file handling method will be put in this class!!
class FileHandler {

    private final String path;
    private String fileName;
    private final Scanner keyboardScan;

    public FileHandler(String p, String fn) {
        path = p;
        fileName = fn;
        keyboardScan = new Scanner(System.in);
    }

    public void productFileProcessLine(String line, Product[] P, int i) {
        try {
            String[] buf = line.split(",");

            String productName = buf[0].trim();
            int productPrice = Integer.parseInt(buf[1].trim());
            int productWeight = Integer.parseInt(buf[2].trim());

            P[i] = new Product(productName, productPrice, productWeight);
        } catch (RuntimeException e) {
            System.out.println(e);
            System.out.println(line + "\n");
        }
    }

    public void wrongProductFile_loop(Product[] P) {
        boolean opensuccess = false;
        int i = 0;
        while (!opensuccess) {
            try (Scanner fileScan = new Scanner(new File(path + fileName))) {
                opensuccess = true;
                while (fileScan.hasNext() && i < 5) {
                    productFileProcessLine(fileScan.nextLine(), P, i);
                    i++;
                }
            } catch (FileNotFoundException e) {
                System.out.println(e);
                System.out.println("New file name = ");
                fileName = keyboardScan.next();
                System.out.print("\n");
            }
        }
    }

    public void orderFileProcessLine(ArrayList<Order> o, String line, ArrayList<Customer> c) {
        try {
            String[] buf = line.split(",");

            int orderNum = Integer.parseInt(buf[0].trim());
            String name = buf[1].trim();
            String shipping = buf[2].trim();
            int order1 = Integer.parseInt(buf[3].trim());
            int order2 = Integer.parseInt(buf[4].trim());
            int order3 = Integer.parseInt(buf[5].trim());
            int order4 = Integer.parseInt(buf[6].trim());
            int order5 = Integer.parseInt(buf[7].trim());

            Order addNew = new Order(orderNum, name, shipping, order1, order2,
                    order3, order4, order5, c);
            o.add(addNew);

        } catch (RuntimeException e) {
            System.out.println(e);
            System.out.println(line + "\n");
        }
    }

    public void wrongOrderFile_loop(ArrayList<Order> o, ArrayList<Customer> c) {
        boolean opensuccess = false;
        while (!opensuccess) {
            try (Scanner fileScan = new Scanner(new File(path + fileName))) {
                opensuccess = true;
                while (fileScan.hasNext()) {
                    orderFileProcessLine(o, fileScan.nextLine(), c);
                }
            } catch (FileNotFoundException e) {
                System.out.println(e);
                System.out.println("New file name = ");
                fileName = keyboardScan.next();
                System.out.print("\n");
            }
        }
    }

    public void shippingFileProcessLine(String line, ArrayList<ShippingCalculator> sc) {
        try {
            String[] buf = line.split(",");

            String shipping_type = buf[0].trim();
            String fee_type = buf[1].trim();
            int min_weight = Integer.parseInt(buf[2].trim());
            int max_weight = Integer.parseInt(buf[3].trim());
            int fee = Integer.parseInt(buf[4].trim());

            ShippingCalculator addNew = new ShippingCalculator(shipping_type, fee_type, min_weight, max_weight, fee);

            sc.add(addNew);

        } catch (RuntimeException e) {
            System.out.println(e);
            System.out.println(line + "\n");
        }
    }

    public void wrongShippingFile_loop(ArrayList<ShippingCalculator> sc) {
        boolean opensuccess = false;
        while (!opensuccess) {
            try (Scanner fileScan = new Scanner(new File(path + fileName))) {
                opensuccess = true;
                while (fileScan.hasNext()) {
                    shippingFileProcessLine(fileScan.nextLine(), sc);
                }
            } catch (FileNotFoundException e) {
                System.out.println(e);
                System.out.println("New file name = ");
                fileName = keyboardScan.next();
                System.out.print("\n");
            }
        }
    }

}
