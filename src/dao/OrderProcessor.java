package dao;

import entity.User;
import entity.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import util.DBConnUtil;

public class OrderProcessor implements IOrderManagementRepository {

    // Create a new user
    @Override
    public void createUser(User user) {
        try (Connection conn = DBConnUtil.getCon()) {
            String query = "INSERT INTO Users (userId, username, password, role) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, user.getUserId());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole());
            ps.executeUpdate();
            System.out.println("User created successfully.");
        } catch (SQLException e) {
            System.out.println("SQL Error creating user: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Unexpected Error creating user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Create a new product
    @Override
    public void createProduct(User user, Product product) {
        // Ensure the user is an admin
        if (user == null || !isAdmin(user)) {
            throw new RuntimeException("Only admins can create products.");
        }

        // Proceed with product creation if admin
        try (Connection conn = DBConnUtil.getCon()) {
            // Check if the product already exists
            String checkProductQuery = "SELECT * FROM Products WHERE productName = ?";
            PreparedStatement psCheck = conn.prepareStatement(checkProductQuery);
            psCheck.setString(1, product.getProductName());
            ResultSet rsCheck = psCheck.executeQuery();
            if (rsCheck.next()) {
                throw new RuntimeException("Product already exists.");
            }

            // Insert the new product
            String query = "INSERT INTO Products (productName, description, price, quantityInStock, type) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, product.getProductName());
            ps.setString(2, product.getDescription());
            ps.setDouble(3, product.getPrice());
            ps.setInt(4, product.getQuantityInStock());
            ps.setString(5, product.getType());
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Product created successfully.");
            } else {
                System.out.println("Failed to create product.");
            }
        } catch (SQLException e) {
            System.out.println("SQL Error creating product: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error creating product: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Helper method to verify if the user is an admin
    private boolean isAdmin(User user) {
        try (Connection conn = DBConnUtil.getCon()) {
            String query = "SELECT role FROM Users WHERE userId = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, user.getUserId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return "Admin".equalsIgnoreCase(rs.getString("role"));
            }
        } catch (SQLException e) {
            System.out.println("SQL Error verifying user role: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Place a new order
    @Override
    public void createOrder(User user, List<Product> products) {
        try (Connection conn = DBConnUtil.getCon()) {
            // Check if user exists
            PreparedStatement userCheckStmt = conn.prepareStatement("SELECT * FROM Users WHERE userId = ?");
            userCheckStmt.setInt(1, user.getUserId());
            ResultSet userResult = userCheckStmt.executeQuery();
            if (!userResult.next()) {
                throw new Exception("User not found!");
            }

            // Insert the new order
            StringBuilder productIds = new StringBuilder();
            for (int i = 0; i < products.size(); i++) {
                if (i > 0) {
                    productIds.append(",");
                }
                productIds.append(products.get(i).getProductId());
            }

            String query = "INSERT INTO Orders (userId, productIds, orderDate) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, user.getUserId());
            ps.setString(2, productIds.toString());
            ps.setDate(3, new java.sql.Date(System.currentTimeMillis()));
            ps.executeUpdate();

            System.out.println("Order created successfully.");
        } catch (SQLException e) {
            System.out.println("SQL Error placing order: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Unexpected Error placing order: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Cancel an order
    @Override
    public void cancelOrder(int userId, int orderId) {
        try (Connection conn = DBConnUtil.getCon()) {
            // Check if the user exists
            PreparedStatement userCheckStmt = conn.prepareStatement("SELECT * FROM Users WHERE userId = ?");
            userCheckStmt.setInt(1, userId);
            ResultSet userResult = userCheckStmt.executeQuery();
            if (!userResult.next()) {
                throw new Exception("User not found!");
            }

            // Check if the order exists
            PreparedStatement orderCheckStmt = conn.prepareStatement("SELECT * FROM Orders WHERE orderId = ?");
            orderCheckStmt.setInt(1, orderId);
            ResultSet orderResult = orderCheckStmt.executeQuery();
            if (!orderResult.next()) {
                throw new Exception("Order not found!");
            }

            // Delete the order
            String query = "DELETE FROM Orders WHERE orderId = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, orderId);
            ps.executeUpdate();

            System.out.println("Order canceled successfully.");
        } catch (SQLException e) {
            System.out.println("SQL Error canceling order: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Unexpected Error canceling order: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Get all products
    @Override
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        try (Connection conn = DBConnUtil.getCon()) {
            String query = "SELECT * FROM Products";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Product product = new Product(
                        resultSet.getInt("productId"),
                        resultSet.getString("productName"),
                        resultSet.getString("description"),
                        resultSet.getDouble("price"),
                        resultSet.getInt("quantityInStock"),
                        resultSet.getString("type")
                );
                products.add(product);
            }
        } catch (SQLException e) {
            System.out.println("SQL Error retrieving products: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Unexpected Error retrieving products: " + e.getMessage());
            e.printStackTrace();
        }
        return products;
    }


//    @Override
//    public List<Product> getOrderByUser(User user) {
//        List<Product> orderedProducts = new ArrayList<>();
//        try (Connection conn = DBConnUtil.getCon()) {
//            // Retrieve orders by user
//            String query = "SELECT productIds FROM Orders WHERE userId = ?";
//            PreparedStatement ps = conn.prepareStatement(query);
//            ps.setInt(1, user.getUserId());
//            ResultSet resultSet = ps.executeQuery();
//
//            while (resultSet.next()) {
//                String productIds = resultSet.getString("productIds");
//                String[] ids = productIds.split(",");
//                for (String id : ids) {
//                    // Retrieve each product by ID
//                    PreparedStatement productStmt = conn.prepareStatement("SELECT * FROM Products WHERE productId = ?");
//                    productStmt.setInt(1, Integer.parseInt(id));
//                    ResultSet productResult = productStmt.executeQuery();
//                    if (productResult.next()) {
//                        Product product = new Product(
//                                productResult.getInt("productId"),
//                                productResult.getString("productName"),
//                                productResult.getString("description"),
//                                productResult.getDouble("price"),
//                                productResult.getInt("quantityInStock"),
//                                productResult.getString("type")
//                        );
//                        orderedProducts.add(product);
//                    }
//                }
//            }
//        } catch (SQLException e) {
//            System.out.println("SQL Error retrieving order by user: " + e.getMessage());
//            e.printStackTrace();
//        } catch (Exception e) {
//            System.out.println("Unexpected Error retrieving order by user: " + e.getMessage());
//            e.printStackTrace();
//        }
//        return orderedProducts;
//    }
    @Override
    public List<Product> getOrderByUser(User user) {
        List<Product> orderedProducts = new ArrayList<>();
        try (Connection conn = DBConnUtil.getCon()) {
            // Retrieve orders by user
            String query = "SELECT productIds FROM Orders WHERE userId = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, user.getUserId());
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                String productIds = resultSet.getString("productIds");
                if (productIds != null && !productIds.trim().isEmpty()) { // Validate productIds string
                    String[] ids = productIds.split(",");
                    for (String id : ids) {
                        try {
                            int productId = Integer.parseInt(id.trim()); // Parse each productId safely
                            // Retrieve each product by ID
                            PreparedStatement productStmt = conn.prepareStatement("SELECT * FROM Products WHERE productId = ?");
                            productStmt.setInt(1, productId);
                            ResultSet productResult = productStmt.executeQuery();
                            if (productResult.next()) {
                                Product product = new Product(
                                        productResult.getInt("productId"),
                                        productResult.getString("productName"),
                                        productResult.getString("description"),
                                        productResult.getDouble("price"),
                                        productResult.getInt("quantityInStock"),
                                        productResult.getString("type")
                                );
                                orderedProducts.add(product);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid product ID format: " + id);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Error retrieving order by user: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Unexpected Error retrieving order by user: " + e.getMessage());
            e.printStackTrace();
        }
        return orderedProducts;
    }

    
}
