<?php

    $CODE = $_GET['code'];
    $NAME = $_GET['name'];
    $PRICE = $_GET['price'];

    $conn = mysqli_connect(
        'localhost',
        'root',
        '',
        'store'
    );

    $sql = "INSERT INTO STOCK(STOCK_CODE, STOCK_NAME, STOCK_PRICE) VALUES()";
    $result = mysqli_query($conn, $sql);

    if($result) {
        echo "200";
    } else {
        echo $result;
    }

?>