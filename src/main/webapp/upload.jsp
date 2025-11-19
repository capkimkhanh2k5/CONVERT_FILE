<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Upload File</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        /* CSS CHỦ ĐẠO CỦA TEAM */
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            background: linear-gradient(135deg, #e8eaf6 0%, #c5e1f5 50%, #f5f5dc 100%);
            min-height: 100vh; position: relative; overflow: hidden;
            display: flex; flex-direction: column;
        }
        .shape { position: absolute; border-radius: 50%; opacity: 0.6; z-index: 0; }
        .shape-1 { width: 150px; height: 150px; background: linear-gradient(45deg, #7c4dff, #448aff); top: 10%; right: 15%; }
        .shape-2 { width: 100px; height: 100px; background: #64ffda; bottom: 10%; left: 10%; opacity: 0.5; }
        
        /* Header */
        header { background: white; padding: 20px 0; box-shadow: 0 2px 10px rgba(0,0,0,0.05); z-index: 10; }
        nav { display: flex; justify-content: space-between; align-items: center; max-width: 1200px; margin: 0 auto; padding: 0 20px; }
        .logo { font-size: 28px; font-weight: bold; text-decoration: none; }
        .logo span:first-child { color: #5e35b1; }
        .logo span:last-child { color: #1e88e5; }

        /* Form Card (Glassmorphism) */
        .upload-container {
            flex: 1;
            display: flex;
            align-items: center;
            justify-content: center;
            z-index: 2;
        }
        .glass-card {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(20px);
            border-radius: 24px;
            padding: 40px;
            width: 100%;
            max-width: 500px;
            box-shadow: 0 15px 35px rgba(0,0,0,0.1);
            border: 1px solid rgba(255, 255, 255, 0.5);
            text-align: center;
        }
        .glass-card h2 {
            color: #333;
            font-weight: 800;
            margin-bottom: 10px;
        }
        .glass-card p { color: #666; margin-bottom: 30px; }

        /* Custom File Input */
        .file-upload-wrapper {
            position: relative;
            width: 100%;
            height: 150px;
            border: 2px dashed #5e35b1;
            border-radius: 15px;
            background: #f8f9fa;
            display: flex;
            align-items: center;
            justify-content: center;
            flex-direction: column;
            margin-bottom: 20px;
            transition: all 0.3s;
            cursor: pointer;
        }
        .file-upload-wrapper:hover { background: #e3f2fd; border-color: #1e88e5; }
        .file-upload-wrapper input {
            position: absolute; width: 100%; height: 100%; opacity: 0; cursor: pointer;
        }
        .icon-upload { font-size: 40px; color: #5e35b1; margin-bottom: 10px; }

        /* Button */
        .btn-convert {
            background: linear-gradient(90deg, #5e35b1, #1e88e5);
            border: none; color: white; padding: 15px; width: 100%;
            border-radius: 12px; font-size: 18px; font-weight: 600;
            box-shadow: 0 5px 15px rgba(94, 53, 177, 0.3);
            transition: transform 0.2s;
        }
        .btn-convert:hover { transform: translateY(-3px); box-shadow: 0 8px 20px rgba(94, 53, 177, 0.4); }
        
        .form-select { padding: 12px; border-radius: 10px; border: 1px solid #ddd; margin-bottom: 20px; }
    </style>
</head>
<body>

    <div class="shape shape-1"></div>
    <div class="shape shape-2"></div>

    <header>
        <nav>
            <a href="#" class="logo"><span>File</span><span>Convert</span></a>
            <a href="jobs" class="text-decoration-none fw-bold" style="color: #5e35b1;">View Progress ></a>
        </nav>
    </header>

    <div class="upload-container">
        <div class="glass-card">
            <h2>Upload Files</h2>
            <p>Choose your file and let us do the magic ✨</p>

            <form action="upload" method="post" enctype="multipart/form-data">
                
                <div class="file-upload-wrapper">
                    <input type="file" name="file" required onchange="document.getElementById('fileName').innerText = this.files[0].name">
                    <div class="icon-upload">☁️</div>
                    <div class="fw-bold text-secondary" id="fileName">Drag & Drop or Click to Browse</div>
                </div>

                <div class="text-start mb-1 fw-bold text-secondary small">Convert to:</div>
                <select name="taskType" class="form-select">
                    <option value="PDF_TO_DOCX">PDF ➝ Word Document</option>
                    <option value="DOCX_TO_PDF">Word ➝ PDF Document</option>
                </select>

                <button type="submit" class="btn-convert">🚀 Start Conversion</button>
            </form>
        </div>
    </div>

</body>
</html>