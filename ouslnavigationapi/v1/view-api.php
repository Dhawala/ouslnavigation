<?php

    require '../includes/connect-db.php';
    
    $course = $_POST["course"];

	$statement = mysqli_prepare($con, "SELECT ac_name, medium, schedule.group, date, start_time, end_time, centre, loc_name, room_name
    FROM schedule
    INNER JOIN activity ON schedule.ac_code = activity.ac_code 
    LEFT JOIN location ON schedule.loc_id = location.loc_id 
    LEFT JOIN room ON location.loc_id = room.loc_id 
    WHERE course_code = ?
    ORDER BY date
    ;");

    mysqli_stmt_bind_param($statement, "s", $course);
    mysqli_stmt_execute($statement);
    $result = mysqli_stmt_get_result($statement);

    $response = array();
    $response["success"] = false;

	while($row = mysqli_fetch_assoc($result)){
        $response["success"] = true;
        $data[] = $row;
    }
    
    $response["data"] = $data;

	echo json_encode($response);

 ?>