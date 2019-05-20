<?php

	require '../includes/connect-db.php';

	$username = $_POST["username"];
	$password = $_POST["password"];

	$statement = mysqli_prepare($con, "SELECT student_login.sno, password, regno, nic, name, email, contact 
	FROM student 
	INNER JOIN student_login ON student.sno = student_login.sno 
	WHERE student_login.sno = ? AND student_login.password = ?");

	mysqli_stmt_bind_param($statement, "ss", $username, $password);
	mysqli_stmt_execute($statement);

	mysqli_stmt_store_result($statement);
	mysqli_stmt_bind_result($statement, $sno, $password, $regno, $nic, $name, $email, $contact);

	$response = array();
	$response["success"] = false;

	while(mysqli_stmt_fetch($statement)){
		$response["success"] = true;
		$response["sno"] = $sno;
		$response["password"] = $password;
		$response["regno"] = $regno;
		$response["nic"] = $nic;
		$response["name"] = $name;
		$response["email"] = $email;
		$response["contact"] = $contact;
	}

	echo json_encode($response);

 ?>