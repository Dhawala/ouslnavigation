<?php

    require '../includes/connect-db.php';
    
	$location = $_POST["location"];

	$statement = mysqli_prepare($con, "SELECT room_name 
	FROM room 
	INNER JOIN location ON room.loc_id = location.loc_id 
	WHERE loc_name = ?
    ;");

    mysqli_stmt_bind_param($statement, "s", $location);
    mysqli_stmt_execute($statement);
    $result = mysqli_stmt_get_result($statement);
	
	$data = null;

    $response = array();
    $response["success"] = false;

	while($row = mysqli_fetch_assoc($result)){
        $response["success"] = true;
        $data[] = $row;
    }
    
    $response["data"] = $data;

	echo json_encode($response);

 ?>