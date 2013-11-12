(function ($) {
    // register namespace
    $.extend(true, window, {
        Tuml: {
            TumlTabQueryManager: TumlTabQueryManager
        }
    });

    function TumlTabQueryManager(parentTumlTabViewManager, instanceQueryUri, classQueryUri, queryId) {

        var tumlQueryGridManager;
        var codeMirror;
        var executeButton;
        this.instanceQueryUri = instanceQueryUri;
        this.classQueryUri = classQueryUri;
        if (queryId !== undefined) {
            this.queryId = queryId;
        }

        this.tumlTabViewManager = parentTumlTabViewManager;

        function init() {
            tumlQueryGridManager = new Tuml.TumlQueryGridManager(parentTumlTabViewManager);
        }

        this.executeQuery = function () {
            var self = this;
            $.ajax({
                url: this.oclExecuteUri + '?query=' + codeMirror.getValue() + '&type=' + executeButton.val(),
                type: "GET",
                contentType: "application/json",
                success: function (data) {
                    self.afterExecuteQuery(data);
                },
                error: function (jqXHR) {
                    $('#serverErrorMsg_' + self.queryTabDivName).addClass('server-error-msg').html(jqXHR.responseText);
                }
            });
        }

        this.afterExecuteQuery = function (data) {
            if (Array.isArray(data)) {
                tumlQueryGridManager.refresh(data[0], this.queryTabDivName + '_' + 'OclResult', true);
            } else {
                var outerDivForResults = $('#' + this.queryTabDivName + '_' + 'OclResult');
                outerDivForResults.children().remove();
                var textAreaResult = $('<textarea />', {id: 'queryResultId'});
                textAreaResult.text(data).appendTo(outerDivForResults);
                CodeMirror.fromTextArea(textAreaResult[0], {mode: 'text/x-less', readOnly: true});
            }
            $('#serverErrorMsg_' + this.queryTabDivName).removeClass('server-error-msg');
            $('#serverErrorMsg_' + this.queryTabDivName).empty();
            $('#tab-container').tabs('resize');
        }

        this.saveToInstance = function () {
            var self = this;
            this.synchronizeQuery(this.queryId);
            this.query.qualifiedName = 'umlglib::org::umlg::query::InstanceQuery';
            var overloadedPostData = {insert: [], update: [], delete: []};
            if (this.query.post) {
                overloadedPostData.insert.push(this.query);
            } else {
                overloadedPostData.update.push(this.query);
            }
            $.ajax({
                url: this.instanceQueryUri,
                type: "POST",
                dataType: "json",
                contentType: "application/json",
                data: JSON.stringify(overloadedPostData),
                success: function (data) {
                    var queryFromDb;
                    var queries = data[0].data;
                    for (var i = 0; i < queries.length; i++) {
                        queryFromDb = queries[i];
                        if (queryFromDb.name == self.query.name) {
                            break;
                        }
                    }
                    if (self.query.post) {
                        self.afterSaveInstance({queryType: 'instanceQuery', query: queryFromDb, gridData: tumlQueryGridManager.getResult()});
                    } else {
                        self.afterUpdateInstance({queryType: 'instanceQuery', query: queryFromDb, gridData: tumlQueryGridManager.getResult()});
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    $('#serverErrorMsg_' + this.queryTabDivName).addClass('server-error-msg').html(jqXHR.responseText);

                }
            });
        }

        this.deleteQuery = function () {
            var self = this;
            this.synchronizeQuery(this.queryId);
            var isInstanceQuery = this.instanceQueryUri !== '' ? true : false;
            this.query.qualifiedName = isInstanceQuery ? 'umlglib::org::umlg::query::InstanceQuery' : 'umlglib::org::umlg::query::ClassQuery';
            var overloadedPostData = {insert: [], update: [], delete: []};
            overloadedPostData.delete.push(this.query);
            $.ajax({
                url: isInstanceQuery ? this.instanceQueryUri : this.classQueryUri,
                type: "POST",
                dataType: "json",
                contentType: "application/json",
                data: JSON.stringify(overloadedPostData),
                success: function (data, textStatus, jqXHR) {
                    if (isInstanceQuery) {
                        self.afterDeleteInstance({queryType: 'instanceQuery', query: self.query, gridData: tumlQueryGridManager.getResult()});
                    } else {
                        self.afterDeleteClassQuery({queryType: 'classQuery', query: self.query, gridData: tumlQueryGridManager.getResult()});
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    $('#serverErrorMsg_' + queryTabDivName).addClass('server-error-msg').html(jqXHR.responseText);
                }
            });
        }

        this.cancelQuery = function () {
            var executeButton = this.queryExecuteButtonFormGroupDiv.find('#executeButton');
            executeButton.text('Execute ' + this.query.type.toUpperCase());
            executeButton.val(this.query.type.toUpperCase());
            codeMirror.setValue(this.query.queryString);
            var queryNameInput = this.queryQueryNameFormGroupDiv.find('#queryName');
            queryNameInput.val(this.query.name);
        }

        this.afterSaveInstance = function (result) {
            this.tumlTabViewManager.afterSaveInstance(result);
        }

        this.afterUpdateInstance = function (result) {
            this.tumlTabViewManager.afterUpdateInstance(result);
        }

        this.afterDeleteInstance = function (result) {
            this.tumlTabViewManager.afterDeleteInstance(result);
        }

        this.afterSaveClassQuery = function (result) {
            this.tumlTabViewManager.afterSaveClassQuery(result);
        }

        this.afterUpdateClassQuery = function (result) {
            this.tumlTabViewManager.afterUpdateClassQuery(result);
        }

        this.afterDeleteClassQuery = function (result) {
            this.tumlTabViewManager.afterDeleteClassQuery(result);
        }

        this.saveToClass = function () {
            var self = this;
            this.synchronizeQuery(this.queryId);
            this.query.qualifiedName = 'umlglib::org::umlg::meta::ClassQuery';
            var overloadedPostData = {insert: [], update: [], delete: []};
            if (this.query.post) {
                overloadedPostData.insert.push(this.query);
            } else {
                overloadedPostData.update.push(this.query);
            }
            $.ajax({
                url: this.classQueryUri,
                type: "POST",
                dataType: "json",
                contentType: "application/json",
                data: JSON.stringify(overloadedPostData),
                success: function (data) {
                    var queryFromDb;
                    var queries = data[0].data;
                    for (var i = 0; i < queries.length; i++) {
                        queryFromDb = queries[i];
                        if (queryFromDb.name == self.query.name) {
                            break;
                        }
                    }

                    if (self.query.post) {
                        self.afterSaveClassQuery({queryType: 'classQuery', query: queryFromDb, gridData: tumlQueryGridManager.getResult()});
                    } else {
                        self.afterUpdateClassQuery({queryType: 'classQuery', query: queryFromDb, gridData: tumlQueryGridManager.getResult()});
                    }

                },
                error: function (jqXHR, textStatus, errorThrown) {
                    $('#serverErrorMsg_' + self.queryTabDivName).addClass('server-error-msg').html(jqXHR.responseText);

                }
            });

        }

        this.showCorrectButtons = function () {
            var self = this;
            if (this.query.queryType === undefined) {
                this.querySaveInstanceFormGroupDiv.show();
                this.querySaveClassFormGroupDiv.show();
                this.queryCancelButtonFormGroupDiv.hide();
                this.queryDeleteButtonFormGroupDiv.hide();
            } else if (this.query.queryType === 'instanceQuery') {
                this.querySaveInstanceFormGroupDiv.show();
                this.querySaveClassFormGroupDiv.hide();
                this.queryCancelButtonFormGroupDiv.show();
                this.queryDeleteButtonFormGroupDiv.show();
            } else {
                this.querySaveInstanceFormGroupDiv.hide();
                this.querySaveClassFormGroupDiv.show();
                this.queryCancelButtonFormGroupDiv.show();
                this.queryDeleteButtonFormGroupDiv.show();
            }

            //Set the execute buttons name and val to reflect the query type
            executeButton = this.queryExecuteButtonFormGroupDiv.find('#executeButton');
            executeButton.text('Execute ' + this.query.type.toUpperCase());
            executeButton.val(this.query.type.toUpperCase());
            executeButton.button().unbind("click");
            executeButton.button().click(
                function () {
                    self.executeQuery();
                }
            );

            //Set the queries name
            var queryNameInput = this.queryQueryNameFormGroupDiv.find('#queryName');
            queryNameInput.val(this.query.name);

            //Wire up the buttons to the correct tab
            var saveInstanceButton = this.querySaveInstanceFormGroupDiv.find('#saveInstanceQueryButton');
            saveInstanceButton.button().unbind("click");
            saveInstanceButton.button().click(
                function () {
                    self.saveToInstance();
                });

            var saveClassButton = this.querySaveClassFormGroupDiv.find('#saveClassQueryButton');
            saveClassButton.button().unbind("click");
            saveClassButton.button().click(
                function () {
                    self.saveToClass();
                });

            var cancelButton = this.queryCancelButtonFormGroupDiv.find('#cancelQueryButton');
            cancelButton.button().unbind("click");
            cancelButton.button().click(
                function () {
                    self.cancelQuery();
                }
            );

            var deleteButton = this.queryDeleteButtonFormGroupDiv.find('#deleteQueryButton');
            deleteButton.button().unbind("click");
            deleteButton.button().click(
                function () {
                    self.deleteQuery();
                }
            );

        }

        this.createQuery = function (queryTabDivName, oclExecuteUri, query, post) {
            var self = this;
            this.queryTabDivName = queryTabDivName;
            this.query = query;
            this.query.post = post;
            this.oclExecuteUri = oclExecuteUri;
            var queryTab = $('#' + queryTabDivName);

            //create a panel with a header and body.
            //header contains the a form for executing and saving the query
            //body contains a layout manager. at the top is the query text and at the bottom the results
            var queryPanel = $('<div />', {class: 'umlg-query-panel panel panel-default'}).appendTo(queryTab);
            var queryPanelBody = $('<div />', {class: 'panel-body'}).appendTo(queryPanel);
            //Create a horizontal inline form for the queries details
            var tabFooter = this.tumlTabViewManager.parentTabContainerManager.tabLayoutTabFooterDiv;
            var queryFormDiv = tabFooter.find('.form-inline');
            this.queryExecuteButtonFormGroupDiv;
            this.queryQueryNameFormGroupDiv;
            this.querySaveInstanceFormGroupDiv;
            this.querySaveClassFormGroupDiv;
            this.queryCancelButtonFormGroupDiv;
            this.queryDeleteButtonFormGroupDiv;
            if (queryFormDiv.length == 0) {
                queryFormDiv = $('<div />', {class: 'form-inline', role: 'form'}).appendTo(tabFooter);
                //Create a split button for the execute
                this.queryExecuteButtonFormGroupDiv = $('<div />', {id: 'queryExecuteButtonFormGroupDiv', class: 'form-group'}).appendTo(queryFormDiv);
                var btnGroup = $('<div />', {class: 'btn-group dropup'}).appendTo(this.queryExecuteButtonFormGroupDiv);
                executeButton = $('<button type="button" id="executeButton" class="btn btn-success umlg-button" />').appendTo(btnGroup);
                $('<span class="glyphicon glyphicon-play-circle"></span>').appendTo(executeButton);
                $('<span />').text(' Execute ' + query.type.toUpperCase()).appendTo(executeButton);
                executeButton.val(query.type.toUpperCase());
                var splitButton = $('<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">').appendTo(btnGroup);
                $('<span class="caret"></span>').appendTo(splitButton);
                var ul = $('<ul class="dropdown-menu" role="menu">').appendTo(btnGroup);

                $('<li><a href="#">OCL</a></li>').appendTo(ul);
                $('<li><a href="#">GREMLIN</a></li>').appendTo(ul);
                $('<li><a href="#">NATIVE</a></li>').appendTo(ul);

                ul.find("li a").click(function () {
                    executeButton.text('Execute ' + $(this).text());
                    executeButton.val($(this).text());
                });

                var querySelectTypeFormGroupDiv = $('<div />', {class: 'form-group'}).appendTo(queryFormDiv);
                $('<label />', {class: 'sr-only', for: queryTabDivName + '_' + 'ExecuteButton'}).text('this is hidden').appendTo(querySelectTypeFormGroupDiv);

                var elementOnTheRight = $('<div />', {class: 'pull-right'}).appendTo(queryFormDiv);

                if (isUmlgLib) {
                    this.queryQueryNameFormGroupDiv = $('<div />', {id: 'queryNameFormDiv', class: 'form-group'}).appendTo(elementOnTheRight);
                    var queryNameInput = $('<input >', {id: 'queryName', type: 'text', class: 'form-control'});
                    queryNameInput.val(query.name).appendTo(this.queryQueryNameFormGroupDiv);

                    this.querySaveInstanceFormGroupDiv = $('<div />', {id: 'saveInstanceFormGroup', class: 'form-group'}).appendTo(elementOnTheRight);
                    var saveInstanceButton = $('<button />', {id: 'saveInstanceQueryButton', class: 'form-control btn btn-primary umlg-button'}).appendTo(this.querySaveInstanceFormGroupDiv);
                    $('<span class="glyphicon glyphicon-save"></span>').appendTo(saveInstanceButton);
                    $('<span />').text(' save to instance').appendTo(saveInstanceButton);

                    this.querySaveClassFormGroupDiv = $('<div />', {id: 'saveClassFormGroup', class: 'form-group'}).appendTo(elementOnTheRight);
                    var saveClassButton = $('<button />', {id: 'saveClassQueryButton', class: 'form-control btn btn-primary umlg-button'}).appendTo(this.querySaveClassFormGroupDiv);
                    $('<span class="glyphicon glyphicon-save"></span>').appendTo(saveClassButton);
                    $('<span />').text(' save to class').appendTo(saveClassButton);

                    this.queryCancelButtonFormGroupDiv = $('<div />', {id: 'cancelButtonFormGroup', class: 'form-group'}).appendTo(elementOnTheRight);
                    var cancelButton = $('<button />', {id: 'cancelQueryButton', class: 'form-control btn btn-primary umlg-button'}).appendTo(this.queryCancelButtonFormGroupDiv);
                    $('<span class="glyphicon glyphicon-ban-circle"></span>').appendTo(cancelButton);
                    $('<span />').text(' cancel').appendTo(cancelButton);
                    this.queryDeleteButtonFormGroupDiv = $('<div />', {id: 'deleteButtonFormGroup', class: 'form-group'}).appendTo(elementOnTheRight);
                    var deleteButton = $('<button />', {id: 'deleteQueryButton', class: 'form-control btn btn-primary umlg-button'}).appendTo(this.queryDeleteButtonFormGroupDiv);
                    $('<span class="glyphicon glyphicon-remove"></span>').appendTo(deleteButton);
                    $('<span />').text(' delete').appendTo(deleteButton);
                }
            } else {
                this.queryExecuteButtonFormGroupDiv = queryFormDiv.find('#queryExecuteButtonFormGroupDiv');
                this.queryQueryNameFormGroupDiv = queryFormDiv.find('#queryNameFormDiv');
                this.querySaveInstanceFormGroupDiv = queryFormDiv.find('#saveInstanceFormGroup');
                this.querySaveClassFormGroupDiv = queryFormDiv.find('#saveClassFormGroup');
                this.queryCancelButtonFormGroupDiv = queryFormDiv.find('#cancelButtonFormGroup');
                this.queryDeleteButtonFormGroupDiv = queryFormDiv.find('#deleteButtonFormGroup');
            }

//            //make sure the
//            queryFormDiv.hide();

            var windowHeight = $('.ui-layout-center').height() - 165;
            var layoutDiv = $('<div />', {id: 'queryLayoutDiv', style: 'height: ' + windowHeight + 'px; width" 100%; overflow: hidden;'});
            layoutDiv.appendTo(queryPanelBody);

            //Create the layout's center and north pane
            var northDiv = $('<div />', {class: 'query-north'});
            var centerDiv = $('<div />', {class: 'query-center'});
            northDiv.appendTo(layoutDiv);
            centerDiv.appendTo(layoutDiv);

            $('<div />', {id: 'serverErrorMsg_' + queryTabDivName}).appendTo(northDiv);

            //Outer div for entering ocl
            var oclOuter = $('<div />', {id: queryTabDivName + '_' + 'OclOuter', class: 'oclouter'});
            oclOuter.appendTo(northDiv);

            //Inner div for entering ocl and buttons
            var oclInner = $('<div />', {id: queryTabDivName + '_' + 'OclInner', class: 'oclinner'}).appendTo(oclOuter);
            var oclTextAreaDiv = $('<div />', {class: 'ocltextarea'}).appendTo(oclInner);
            var textArea = $('<textarea />', {id: queryTabDivName + '_' + 'QueryString'});
            textArea.text(query.queryString);
            textArea.appendTo(oclTextAreaDiv);
            codeMirror = CodeMirror.fromTextArea(textArea[0], {
                lineNumbers: true,
                matchBrackets: true,
                mode: "text/x-groovy",
                onKeyEvent: function (o, e) {
                if ((e.which === 13 && e.altKey && e.type === "keydown") || (e.which === 13 && e.ctrlKey && e.type === "keydown")) {
                    self.executeQuery();
                    e.preventDefault();
                }
            }});
//            codeMirror.setOption("theme", "3024-day");

            //Outer div for results
            var oclResult = $('<div />', {id: queryTabDivName + '_' + 'OclResult', class: 'oclresult'});
            oclResult.appendTo(centerDiv);
            if (query.data !== undefined && query.data !== null) {
                tumlQueryGridManager.refresh(query.data, queryTabDivName + '_' + 'OclResult');
            }

            layoutDiv.layout({
                center__paneSelector: ".query-center",
                north__paneSelector: ".query-north",
                north__size: 125,
                spacing_open: 3,

                onresize_end: function () {
                    //Resize the textarea
                    var northHeight = $('.query-north').height() - 15;
                    $('.CodeMirror').height(northHeight);
                    return true;
                }
            });

        }

        this.synchronizeQuery = function(id) {
            this.query.name = this.queryQueryNameFormGroupDiv.find('#queryName').val();
            this.query.queryString = codeMirror.getValue();
            this.query.queryEnum = executeButton.val();
            if (id !== undefined) {
                this.query.id = id;
            }
        }

        //Public api
        $.extend(this, {
            "TumlTabQueryManagerVersion": "1.0.0",
            //These events are propagated from the grid
            "onSelfCellClick": new Tuml.Event()
        });

        init();
    }

})(jQuery);