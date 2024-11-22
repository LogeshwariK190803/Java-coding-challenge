import dao.OrderProcessor;
import dao.IOrderManagementRepository;
import entity.User;
import entity.Product;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class OrderManagement {
    public static void main(String[] args) {
        IOrderManagementRepository orderProcessor = new OrderProcessor();
        Scanner scanner = new Scanner(System.in);
        int choice;

        // Loop for menu-driven interaction
        while (true) {
            System.out.println("Choose an option:");
            System.out.println("1. Create User");
            System.out.println("2. Create Product");
            System.out.println("3. Create Order");
            System.out.println("4. Cancel Order");
            System.out.println("5. View Products");
            System.out.println("6. View Orders by User");
            System.out.println("7. Exit");

            choice = scanner.nextInt();
            scanner.nextLine(); // consume newline character after int input

            switch (choice) {
                case 1:
                    // Create user
                    System.out.println("Enter user ID: ");
                    int userId = scanner.nextInt();
                    scanner.nextLine(); // consume newline character
                    System.out.println("Enter username: ");
                    String username = scanner.nextLine();
                    System.out.println("Enter password: ");
                    String password = scanner.nextLine();
                    System.out.println("Enter role (Admin/User): ");
                    String role = scanner.nextLine();
                    User newUser = new User(userId, username, password, role);
                    orderProcessor.createUser(newUser);
                    break;

                case 2:
                    // Create product
                    System.out.println("Enter admin user ID: ");
                    int adminUserId = scanner.nextInt();
                    scanner.nextLine(); // consume newline character
                    System.out.println("Enter admin username: ");
                    String adminUsername = scanner.nextLine();
                    System.out.println("Enter admin role: ");
                    String adminRole = scanner.nextLine();
                    
                    User adminUser = new User(adminUserId, adminUsername, "password", adminRole);
                    
                    if ("Admin".equalsIgnoreCase(adminRole)) {
                        System.out.println("Enter product ID: ");
                        int productId = scanner.nextInt();
                        scanner.nextLine(); // consume newline character
                        System.out.println("Enter product name: ");
                        String productName = scanner.nextLine();
                        System.out.println("Enter product description: ");
                        String description = scanner.nextLine();
                        System.out.println("Enter product price: ");
                        double price = scanner.nextDouble();
                        System.out.println("Enter product quantity: ");
                        int quantityInStock = scanner.nextInt();
                        scanner.nextLine(); // consume newline character
                        System.out.println("Enter product type (Electronics/Clothing): ");
                        String type = scanner.nextLine();
                        Product newProduct = new Product(productId, productName, description, price, quantityInStock, type);
                        orderProcessor.createProduct(adminUser, newProduct); // This calls the actual createProduct method
                    } else {
                        System.out.println("Only admins can create products.");
                    }
                    break;


                case 3:
                    // Create order
                    System.out.println("Enter user ID for order: ");
                    int orderUserId = scanner.nextInt();
                    scanner.nextLine(); // consume newline character
                    System.out.println("Enter product IDs for order (comma-separated): ");
                    String[] productIds = scanner.nextLine().split(",");
                    
                    List<Product> orderProducts = new ArrayList<>();
                    List<Product> allProducts = orderProcessor.getAllProducts();  // Get all products
                    for (String id : productIds) {
                        int prodId = Integer.parseInt(id.trim());
                        // Fetch product by ID from the list of all products
                        for (Product p : allProducts) {
                            if (p.getProductId() == prodId) {
                                orderProducts.add(p);
                            }
                        }
                    }
                    
                    User orderUser = new User(orderUserId, "user", "password", "User");  // Use the correct user information
                    orderProcessor.createOrder(orderUser, orderProducts);
                    break;

                case 4:
                    // Cancel order
                    System.out.println("Enter user ID: ");
                    int cancelUserId = scanner.nextInt();
                    System.out.println("Enter order ID to cancel: ");
                    int orderId = scanner.nextInt();
                    orderProcessor.cancelOrder(cancelUserId, orderId);
                    break;

                case 5:
                    // View all products
                    List<Product> allProducts1 = orderProcessor.getAllProducts();
                    for (Product p : allProducts1) {
                        System.out.println(p);
                    }
                    break;

                case 6:
                    // View orders by user
                    System.out.println("Enter user ID to view orders: ");
                    int viewUserId = scanner.nextInt();
                    User viewUser = new User(viewUserId, "sample", "password", "User");
                    List<Product> orderedProducts = orderProcessor.getOrderByUser(viewUser);
                    for (Product p : orderedProducts) {
                        System.out.println(p);
                    }
                    break;

                case 7:
                    // Exit the program
                    System.out.println("Exiting...");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid option, try again.");
            }
        }
    }
}
