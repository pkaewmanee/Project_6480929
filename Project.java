/*
Possathorn Sujipisut 6480274
Phakkhapon Kaewmanee 6480929
Supakorn Unjindamanee 6480279
Jawit Poopradit      6480087
 */
package Project_6480929;

import java.io.*;
import java.util.*;

public class Project {

    public static void main(String args[]) {

        Product P[] = new Product[5];
        String path = "src/main/java/Project_6480929/";
        String productInFile = "products.txt";
        //String orderInFile = "orders_errors.txt";
         String orderInFile = "orders.txt";
        String shippingInFile = "shipping.txt";

        FileHandler FH = new FileHandler(path, productInFile);
        FH.wrongProductFile_loop(P);

        ArrayList<ShippingCalculator> shipping = new ArrayList<>();
        FileHandler SHF = new FileHandler(path, shippingInFile);
        SHF.wrongShippingFile_loop(shipping);

        //GET RID ONCE HAVE SHIPPING
        //Handling Orders Errors
        ArrayList<Order> orders = new ArrayList<>();
        FileHandler OFH = new FileHandler(path, orderInFile);

        //Adding each new customer
        ArrayList<Customer> c = new ArrayList<>();
        OFH.wrongOrderFile_loop(orders, c);

        //Order processing and printing
        System.out.printf("\n==== Order Processing ====");
        for (Order o : orders) {
            o.orderProcessing(P, c, shipping);
        }
        /*System.out.println("===== Product Summary =====");
        for(int t = 0 ; t<5 ; t++){
            System.out.printf("Total item %d: ", t+1); System.out.println(P[t].returnUnits());
        }*/
        Arrays.sort(P, (p1, p2) -> p2.calculateTotalSales(p2.returnUnits()) - p1.calculateTotalSales(p1.returnUnits()));
        System.out.println("===== Product Summary =====");
        for (int t = 0; t < 5; t++) {
            Product currentProduct = P[t];
            System.out.printf("%17s  %15s %,7d, %2d units\n", currentProduct.returnName(), "Total sales:", currentProduct.calculateTotalSales(currentProduct.returnUnits()), currentProduct.returnUnits());
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

    public int calculateTotalUnits(int units) {
        totalUnits += units;
        return totalUnits;
    }

    public int returnUnits() {
        return totalUnits;
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

    public int cashBackRedemption(int totalBills) {
        int MaxCashBacks = Math.max(0, Math.min(cashBacks, 100));
        int redemption = Math.min(MaxCashBacks, totalBills);
        cashBacks -= redemption;
        return redemption;
    }

    public void addCashBacks(int totalPrices) {
        double earnCashBack = Math.floor(totalPrices * 0.01);
        int cashBacksInt = (int) cashBacks;  //get rid of demimal
        cashBacks += earnCashBack;
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

    public int getItem1() {
        return item1;
    }

    public void orderProcessing(Product p[], ArrayList<Customer> c, ArrayList<ShippingCalculator> shippingcal) {
        totalPrice = 0;
        totalWeight = 0;

        int[] itemQuantities = {item1, item2, item3, item4, item5};

        // Calculate total quantity of each item
        for (int i = 0; i < p.length; i++) {
            p[i].calculateTotalUnits(itemQuantities[i]);
        }

        //Calcualte total price and weight
        for (int i = 0; i < p.length; i++) {
            int item = 0;
            switch (i) {
                case 0:
                    item = item1;
                    break;
                case 1:
                    item = item2;
                    break;
                case 2:
                    item = item3;
                    break;
                case 3:
                    item = item4;
                    break;
                case 4:
                    item = item5;
                    break;
            }
            totalPrice += p[i].returnPrice() * item;
            totalWeight += p[i].returnWeight() * item;
        }

        //Cashback System
        Customer customer = null;
        for (Customer cust : c) {
            if (cust.returnName().equals(orderName)) {
                customer = cust;
                break;
            }
        }
        if (customer == null) {
            customer = new Customer(orderName);
            c.add(customer);
        }

        int currentCashback = customer.returnCashback();
        int redeemedCashback = customer.cashBackRedemption(totalPrice);
        int discountedPrice = totalPrice - redeemedCashback;
        customer.addCashBacks(totalPrice);
        int futureCashback = customer.returnCashback();

        //CHANGE FOR SHIPPING
        int shippingId;
        String shippingType;
        int shippingPrice = 0;
        if (shipping.equalsIgnoreCase("S")) {
            shippingId = 0;
            shippingType = "(standard)";
        } else {
            shippingId = 1;
            shippingType = "(express)";
        }

        int shippingFee = ShippingCalculator.Calculate(shippingId, shippingType, totalWeight, shipping, shippingPrice, shippingcal);
        int finalBill = discountedPrice + shippingFee;

        System.out.printf("\nOrder %2d%11s,%6s >> ", orderNumber, shippingType, orderName);
        System.out.printf("%17s(%2d)%17s(%2d)%17s(%2d)%17s(%2d)%17s(%2d)", p[0].returnName(), item1, p[1].returnName(), item2,
                p[2].returnName(), item3, p[3].returnName(), item4,
                p[4].returnName(), item5);
        System.out.printf("\n%40s%6s", ">> Available cashback = ", String.format("%,d", currentCashback));
        System.out.printf("\n%40s%6s", ">> Total price = ", String.format("%,d", totalPrice));
        System.out.printf("\n%40s%6s%25s%s", ">> Total weight = ", String.format("%,d", totalWeight), "Shipping Fee = ", String.format("%,d", shippingFee));
        System.out.printf("\n%40s%6s%36s%s\n", ">> Final bill = ", String.format("%,d", finalBill), "Cashback for next order = ", String.format("%,d", futureCashback));
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

class InvalidInputException extends RuntimeException {

    public InvalidInputException(String errorMessage) {
        super(errorMessage);
    }
}

class MissingFormatException extends java.lang.ArrayIndexOutOfBoundsException {

    public MissingFormatException(String errorMessage) {
        super(errorMessage);
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
        System.out.println("Read products from file " + path + fileName + "\n");
        while (!opensuccess) {
            try (Scanner fileScan = new Scanner(new File(path + fileName))) {
                opensuccess = true;
                while (fileScan.hasNext() && i < 5) {
                    productFileProcessLine(fileScan.nextLine(), P, i);
                    i++;
                }
            } catch (FileNotFoundException e) {
                System.out.println(e);
                System.out.println("\nEnter file name for products: ");
                fileName = keyboardScan.next();
                File inputFile = new File(path + fileName);
                if (inputFile.exists()) {
                    //fileName = inputFileName;
                    System.out.println("Read products from file " + path + fileName);
                }
                System.out.print("\n");
            }

        }
    }

    public void orderFileProcessLine(ArrayList<Order> o, String line, ArrayList<Customer> c) {
        boolean correctionMade = false;
        try {
            String[] buf = line.split(",");

            int orderNum = Integer.parseInt(buf[0].trim());
            String name = buf[1].trim();
            String shipping = buf[2].trim();

            int[] order = new int[5];
            int i, j = 0;

            for (i = 0; i < 5; i++) {
                try {
                    if (buf.length < 8 && shipping.matches("\\d+")) {
                        shipping = "S";
                    }

                    if (!"E".equalsIgnoreCase(shipping) && !"S".equalsIgnoreCase(shipping) && shipping.matches("\\d+")) {
                        order[0] = Integer.parseInt(buf[2].trim());
                        throw new InvalidInputException("For input: " + buf[2].trim());
                    } else if (!"E".equalsIgnoreCase(shipping) && !"S".equalsIgnoreCase(shipping)) {
                        shipping = "S";
                        order[i] = Integer.parseInt(buf[i + 3].trim());
                        throw new InvalidInputException("For input: " + buf[2].trim());
                    } else {
                        order[i] = Integer.parseInt(buf[i + 3 - j].trim());
                    }

                    if (order[i] < 0) {
                        order[i] = 0;
                        throw new InvalidInputException("For input: " + buf[i + 3].trim());
                    }
                    
                } catch (NumberFormatException | InvalidInputException | MissingFormatException e) {
                    correctionMade = true;
                    System.out.println(e);
                } catch (ArrayIndexOutOfBoundsException e) {
                    e = new MissingFormatException(" 1 columns missing");
                    correctionMade = true;
                    System.out.println(e);
                    order[i] = 0;
                }
            }
            if (correctionMade) {
                System.out.print("Original [" + line + "] =========> ");
                System.out.printf("Correction [%d, %s, %s, %d, %d, %d, %d, %d]\n\n", orderNum, name, shipping,
                        order[0], order[1], order[2], order[3], order[4]);
            }

            Order addNew = new Order(orderNum, name, shipping, order[0], order[1],
                    order[2], order[3], order[4], c);
            o.add(addNew);

        } catch (RuntimeException e) {
            System.out.println(e);
            System.out.println(line + "\n");
        }
    }

    public void wrongOrderFile_loop(ArrayList<Order> o, ArrayList<Customer> c) {
        boolean opensuccess = false;
        System.out.println("Read orders from file " + path + fileName + "\n");
        while (!opensuccess) {
            try (Scanner fileScan = new Scanner(new File(path + fileName))) {
                opensuccess = true;
                while (fileScan.hasNext()) {
                    orderFileProcessLine(o, fileScan.nextLine(), c);
                }
            } catch (FileNotFoundException e) {
                System.out.println(e);
                System.out.println("\nEnter file name for orders: ");
                fileName = keyboardScan.next();
                File inputFile = new File(path + fileName);
                if (inputFile.exists()) {
                    System.out.println("Read orders from file " + path + fileName);
                }
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
        System.out.println("Read shipping from file " + path + fileName + "\n");
        while (!opensuccess) {
            try (Scanner fileScan = new Scanner(new File(path + fileName))) {
                opensuccess = true;
                while (fileScan.hasNext()) {
                    shippingFileProcessLine(fileScan.nextLine(), sc);
                }
            } catch (FileNotFoundException e) {
                System.out.println(e);
                System.out.println("\nEnter file name for shipping: ");
                fileName = keyboardScan.next();
                File inputFile = new File(path + fileName);
                if (inputFile.exists()) {
                    System.out.println("Read products from file " + path + fileName);
                }
                System.out.print("\n");
            }
        }
    }

}
