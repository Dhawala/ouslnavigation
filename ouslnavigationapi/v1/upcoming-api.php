<?php

    require '../includes/connect-db.php';
    
	$sno = $_POST["sno"];
	$datefrom = $_POST["datefrom"];
	$dateto = $_POST["dateto"];

	$statement = mysqli_prepare($con, "SELECT schedule.course_code, ac_name, medium, schedule.group, date, start_time, end_time, centre, loc_name, room_name
	FROM schedule
	INNER JOIN course ON schedule.course_code = course.course_code
	INNER JOIN enrollment ON course.course_code = enrollment.course_code
	INNER JOIN activity ON schedule.ac_code = activity.ac_code
	LEFT JOIN location ON schedule.loc_id = location.loc_id
	LEFT JOIN room ON location.loc_id = room.loc_id
	WHERE sno = ? AND date > ? AND date < date_add(?, INTERVAL 1 MONTH)
	ORDER BY date
    ;");

    mysqli_stmt_bind_param($statement, "sss", $sno, $datefrom, $dateto);
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