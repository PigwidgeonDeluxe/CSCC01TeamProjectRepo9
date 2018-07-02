<!DOCTYPE html>
<html lang="en">
<head>
	<title> File Upload</title>
	<meta http-equiv="Content-Type" content="text/html?; charset=UTF-8">
</head>
<body>
	<form action ="upload" method="POST" enctype="multipart/form-data">
		FILE:
		<input type="file" name=file multiple id="file"/>
		<input type="submit" value="Upload" name="upload" id="upload" />
	</form>
</body>
</html>
