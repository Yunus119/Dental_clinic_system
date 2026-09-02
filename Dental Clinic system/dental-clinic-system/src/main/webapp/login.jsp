<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Dental Clinic - Login</title>
    <link rel="stylesheet" href="css/app-style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <style>
        .login-wrapper {
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            background: var(--color-bg-light);
        }
        .login-card {
            background: var(--color-white);
            border-radius: var(--radius-card);
            box-shadow: 0 10px 40px rgba(0,0,0,0.08);
            padding: 50px 45px;
            width: 100%;
            max-width: 400px;
            text-align: center;
        }
        .login-card .brand-icon {
            font-size: 40px;
            color: var(--color-accent);
            margin-bottom: 10px;
        }
        .login-card h2 {
            margin-bottom: 5px;
        }
        .login-card p.subtitle {
            margin-bottom: 30px;
            font-size: 13px;
        }
        .login-card form {
            text-align: left;
        }
        .login-card .btn {
            width: 100%;
            margin-top: 10px;
        }
    </style>
</head>
<body>

    <div class="login-wrapper">
        <div class="login-card">

            <div class="brand-icon"><i class="fa fa-h-square"></i></div>
            <h2>Dental Clinic System</h2>
            <p class="subtitle">Sign in to continue</p>

            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-error"><%= request.getAttribute("error") %></div>
            <% } %>

            <form action="login" method="post">
                <div class="form-group">
                    <label>Username</label>
                    <input type="text" name="username" required>
                </div>

                <div class="form-group">
                    <label>Password</label>
                    <input type="password" name="password" required>
                </div>

                <button type="submit" class="btn btn-primary">Login</button>
            </form>

        </div>
    </div>

</body>
</html>
