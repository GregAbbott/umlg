<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="shortcut icon" href="../../assets/ico/favicon.ico">

    <title>Umlg</title>

    <!-- Bootstrap core CSS -->
    <link href="bootstrap/bootstrap/css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom styles for this template -->
    <link href="stylesheets/stylesheets/offcanvas.css" rel="stylesheet">

    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
    <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>

<body>
<div class="navbar navbar-fixed-top navbar-inverse" role="navigation">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="index.html">UMLG</a>
        </div>
        <div class="collapse navbar-collapse">
            <ul class="nav navbar-nav pull-right">
                <li><a href="documentation.html">Documentation</a></li>
                <li><a href="https://github.com/pietermartin/umlg">Github</a></li>
                <li><a href="http://demo.umlg.org">Demo</a></li>
            </ul>
        </div>
        <!-- /.nav-collapse -->
    </div>
    <!-- /.container -->
</div>
<!-- /.navbar -->

<div class="container">
    <div class="jumbotron">
        <p>Umlg is a UML to java code generator. From class diagrams, persistent java entities are generated. The
            entities persist via an embedded
            <a href="http://blueprints.tinkerpop.com/">Blueprints</a> graph databases. The semantics of a
            <a href="https://github.com/tinkerpop/blueprints/wiki/Property-Graph-Model">Property Graph Model</a> is a
            natural fit for implementing the rich semantics of UML class diagrams in java.</p>
    </div>

    <div class="container">
        <!-- Example row of columns -->
        <div class="row">
            <div class="col-md-4">
                <h2>UML</h2>

                <p><a href="http://www.omg.org/spec/UML/2.4.1/Superstructure/PDF">Demo1</a></p>

            </div>
            <div class="col-md-4">
                <h2>OCL</h2>

                <p>Umlg has full support for <a href="http://www.omg.org/spec/OCL/2.3.1/PDF">OCL</a> (object constraint language). Constraints, derived properties and
                    query operations can be fully specified in OCL. For each OCL expression, java is generated directly
                    into the owning entity.</p>

            </div>
            <div class="col-md-4">
                <h2>Rest Interface and UI</h2>

                <p>Umlg can optionally generate a rest interface to the application for performing remote crud and query
                    operations. Utilizing the rest interface a web based ui is provided. From the gui crud and query
                    operations can be executed. This makes it easy to visualize, use and maintain the application.</p>

            </div>

        </div>
        <div class="row">
            <div class="col-md-4">
                <h2>Entities</h2>

                <p>Java entities are interceptor and annotation free. Together with the performance of graph databases
                    and super fast start up times, development is a joy again.</p>

            </div>

            <div class="col-md-4">
                <h2>Queries</h2>

                <p>Queries can be executed in OCL, <a href="https://github.com/tinkerpop/gremlin/wiki">Gremlin</a> or the
                    underlying blueprint graph's native query language.</p>

            </div>

            <div class="col-md-4">
                <h2>Blueprints</h2>

                <p>Umlg is based on the pure <a href="http://blueprints.tinkerpop.com/">Blueprints</a> api. As such any
                    Blueprints enabled graph database will work. The Travis CI test suite currently executes successfully
                    on <a href="https://bitbucket.org/lambdazen/bitsy/wiki/Home">Bitsy</a>,
                    <a href="http://www.orientdb.org/">OrientDb</a>, <a href="http://www.neo4j.org/">Neo4j</a> and
                    <a href="https://github.com/thinkaurelius/titan/wiki">Titan</a>.</p>

            </div>

        </div>

        <hr>
        <footer>
            <p>&copy; Company 2014</p>
        </footer>

    </div>
    <!--/.container-->


    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="https://code.jquery.com/jquery-1.10.2.min.js"></script>
    <script src="bootstrap/bootstrap/js/bootstrap.min.js"></script>
    <script src="javascripts/javascripts/offcanvas.js"></script>
</div>
</body>
</html>
