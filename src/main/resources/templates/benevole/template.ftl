<#macro mainLayout>
    <!doctype html>
    <html lang="fr">
    <head>
        <title>FESTIVAL</title>
        <!-- Bootstrap 5.1.3 -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="icon" type="image/x-icon" href="../../static/img/favicon.ico"/>
        <!-- Font Awesome icons (free version)-->
        <script src="https://use.fontawesome.com/releases/v6.1.0/js/all.js" crossorigin="anonymous"></script>
        <!-- Google fonts-->
        <link href="https://fonts.googleapis.com/css?family=Varela+Round" rel="stylesheet"/>
        <link href="https://fonts.googleapis.com/css?family=Nunito:200,200i,300,300i,400,400i,600,600i,700,700i,800,800i,900,900i"
              rel="stylesheet"/>
        <!-- Core theme CSS (includes Bootstrap)-->
        <link href="/static/css/styles.css" rel="stylesheet"/>
        <link href="/static/css/css.css" rel="stylesheet"/>
        <link href="/static/css/gestion.css" rel="stylesheet"/>
    </head>
    <body id="page-top">
    <nav class="navbar navbar-expand-lg navbar-light fixed-top" id="mainNav">
        <div class="container px-4 px-lg-5">
            <a class="navbar-brand" href="/static/accueil_stand.html">FESTIVAL</a>

            <button class="navbar-toggler navbar-toggler-right" type="button" data-bs-toggle="collapse"
                    data-bs-target="#navbarResponsive" aria-controls="navbarResponsive" aria-expanded="false"
                    aria-label="Toggle navigation">
                Menu
                <i class="fas fa-bars"></i>
            </button>
            <div class="collapse navbar-collapse" id="navbarResponsive">
                <ul class="navbar-nav ms-auto">
                    <li class="nav-item"><a class="nav-link" href="/deconnexion">Déconnexion</a></li>
                    <li class="nav-item"><a class="nav-link" href="/historique_stand">Historique des achats</a></li>
                    <li class="nav-item"><a class="nav-link" href="/gestion">Gestion</a></li>
                </ul>
            </div>
        </div>
    </nav>
    <#nested/>
    <!-- Bootstrap core JS-->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <!-- Core theme JS-->
    <script src="../../static/js/scripts.js"></script>
    <script src="https://cdn.startbootstrap.com/sb-forms-latest.js"></script>
    </body>
    </html>
</#macro>