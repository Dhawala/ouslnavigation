<?php

    require '../includes/connect-db.php';

	$statement = mysqli_prepare($con, "SELECT * FROM building;");

    mysqli_stmt_execute($statement);
    $result = mysqli_stmt_get_result($statement);

    $response = array();
    $response["success"] = false;

	while($row = mysqli_fetch_assoc($result)){
        $response["success"] = true;
        $buildings[] = $row;
    }
    
    $response["buildings"] = $buildings;

	echo json_encode($response);

 ?>