<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>File Upload - CONVERT_FILE</title>
</head>
<body>
    <h2>Upload PDF hoặc ZIP</h2>
    <form method="post" action="upload" enctype="multipart/form-data">
        <input type="file" name="file" required />
        <button type="submit">Tải lên</button>
    </form>

    <p style="color:green;">
        ${message}
    </p>
</body>
</html>
