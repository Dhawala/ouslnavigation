<?php

    require '../includes/connect-db.php';

    $statement1 = mysqli_prepare($con, "SELECT GREATEST(MAX(loc_no_from), MAX(loc_no_to)) AS max_value FROM route;");
    $statement2 = mysqli_prepare($con, "SELECT * FROM route;");

    mysqli_stmt_execute($statement1);

	mysqli_stmt_store_result($statement1);
	mysqli_stmt_bind_result($statement1, $max_value);

	$response = array();
    $response["success"] = false;
    
    while(mysqli_stmt_fetch($statement1)){
		$response["success"] = true;
		$response["max_value"] = $max_value;
    }
    
    mysqli_stmt_execute($statement2);
    $result = mysqli_stmt_get_result($statement2);

	while($row = mysqli_fetch_assoc($result)){
        $response["success"] = true;
        $routes[] = $row;
    }
    
    $response["routes"] = $routes;

	echo json_encode($response);

 ?>