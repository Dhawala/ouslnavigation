<?php

    require '../includes/connect-db.php';

	$statement = mysqli_prepare($con, "SELECT * FROM location;");

    mysqli_stmt_execute($statement);
    $result1 = mysqli_stmt_get_result($statement);

    $response = array();
    $response["success"] = false;

	while($row = mysqli_fetch_assoc($result1)){
        $response["success"] = true;
        $locations[] = $row;
    }
    
    $response["locations"] = $locations;

	echo json_encode($response);

 ?>