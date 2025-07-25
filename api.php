<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET, POST");
header("Access-Control-Allow-Headers: Content-Type");

$host = "localhost";
$user = "root";
$pass = "";
$db = "food_ordering";

$conn = new mysqli($host, $user, $pass, $db);

if ($conn->connect_error) {
    echo json_encode(["error" => "Database connection failed: " . $conn->connect_error]);
    exit;
}

if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    $action = $_GET['action'] ?? '';

    if ($action === 'get_menu') {
        $result = $conn->query("SELECT * FROM menu_items");
        $menu = [];
        while ($row = $result->fetch_assoc()) {
            $menu[] = $row;
        }
        echo json_encode($menu);

    } elseif ($action === 'get_orders') {
        $orders = [];
        $orderResult = $conn->query("SELECT * FROM orders ORDER BY created_at DESC");

        while ($order = $orderResult->fetch_assoc()) {
            $orderId = $order['id'];
            $items = [];

            $itemResult = $conn->query("SELECT m.name, oi.quantity 
                FROM order_items oi 
                JOIN menu_items m ON oi.item_id = m.id 
                WHERE oi.order_id = $orderId");

            while ($item = $itemResult->fetch_assoc()) {
                $items[] = $item;
            }

            $orders[] = [
                "order_id" => $orderId,
                "status" => $order['status'],
                "items" => $items
            ];
        }

        echo json_encode($orders);
    }

} elseif ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $data = json_decode(file_get_contents("php://input"), true);
    $action = $data['action'] ?? '';

    if ($action === 'insert_cart') {
        try {
            $conn->begin_transaction();

            $insertOrder = $conn->query("INSERT INTO orders (status) VALUES ('Pending')");
            if (!$insertOrder) {
                throw new Exception("Failed to insert order: " . $conn->error);
            }

            $orderId = $conn->insert_id;

            foreach ($data['items'] as $item) {
                $itemId = intval($item['item_id']);
                $quantity = intval($item['quantity']);

                $stmt = $conn->prepare("INSERT INTO order_items (order_id, item_id, quantity) VALUES (?, ?, ?)");
                if (!$stmt) {
                    throw new Exception("Prepare failed: " . $conn->error);
                }

                $stmt->bind_param("iii", $orderId, $itemId, $quantity);
                if (!$stmt->execute()) {
                    throw new Exception("Execute failed: " . $stmt->error);
                }
            }

            $conn->commit();
            echo json_encode(["status" => "success", "order_id" => $orderId]);

        } catch (Exception $e) {
            $conn->rollback();
            echo json_encode(["error" => $e->getMessage()]);
        }

    } elseif ($action === 'make_payment') {
        $orderId = intval($data['order_id']);
        $paymentMethod = $conn->real_escape_string($data['payment_method']);
        $amount = floatval($data['amount']);

        $stmt = $conn->prepare("INSERT INTO payments (order_id, payment_method, amount) VALUES (?, ?, ?)");
        if (!$stmt) {
            echo json_encode(["error" => "Prepare failed: " . $conn->error]);
            exit;
        }

        $stmt->bind_param("isd", $orderId, $paymentMethod, $amount);
        if ($stmt->execute()) {
            echo json_encode(["status" => "success", "payment_id" => $conn->insert_id]);
        } else {
            echo json_encode(["error" => "Insert failed: " . $stmt->error]);
        }

    } elseif ($action === 'update_order_status') {
        $orderId = intval($data['order_id']);
        $newStatus = $conn->real_escape_string($data['new_status']);

        $update = $conn->query("UPDATE orders SET status = '$newStatus' WHERE id = $orderId");

        if ($update) {
            echo json_encode(["status" => "success"]);
        } else {
            echo json_encode(["error" => "Update failed: " . $conn->error]);
        }

    } elseif ($action === 'add_menu_item') {
        $name = $conn->real_escape_string($data['name']);
        $price = floatval($data['price']);
        $category = $conn->real_escape_string($data['category']);

        $query = "INSERT INTO menu_items (name, price, category) VALUES ('$name', $price, '$category')";
        if ($conn->query($query)) {
            echo json_encode(["status" => "success"]);
        } else {
            echo json_encode(["error" => $conn->error]);
        }

    } elseif ($action === 'update_menu_item') {
        $id = intval($data['id']);
        $name = $conn->real_escape_string($data['name']);
        $price = floatval($data['price']);
        $category = $conn->real_escape_string($data['category']);

        $query = "UPDATE menu_items SET name='$name', price=$price, category='$category' WHERE id=$id";
        if ($conn->query($query)) {
            echo json_encode(["status" => "success"]);
        } else {
            echo json_encode(["error" => $conn->error]);
        }

    } elseif ($action === 'delete_menu_item') {
        $id = intval($data['id']);

        $query = "DELETE FROM menu_items WHERE id = $id";
        if ($conn->query($query)) {
            echo json_encode(["status" => "success"]);
        } else {
            echo json_encode(["error" => $conn->error]);
        }
    }
}

$conn->close();
