<?php

    require '../includes/connect-db.php';
    
	$room = $_POST["room"];

	$statement = mysqli_prepare($con, "SELECT description  
	FROM room 
	WHERE room_name = ?
    ;");

    mysqli_stmt_bind_param($statement, "s", $room);
    mysqli_stmt_execute($statement);
    
	mysqli_stmt_store_result($statement);
	mysqli_stmt_bind_result($statement, $desc);

    $response = array();
    $response["success"] = false;

	while(mysqli_stmt_fetch($statement)){
        $response["success"] = true;
        $response["desc"] = $desc;
    }

	echo json_encode($response);

 ?>