(function ($) {
    // register namespace
    $.extend(true, window, {
        Tuml:{
            TumlManyViewManager:TumlManyViewManager
        }
    });

    function TumlManyViewManager() {

        var self = this;
        var tumlTabViewManagers = [];

        function init() {
        }

        function refresh(tumlUri, result, propertyNavigatingTo, isOne, forCreation) {
            tumlTabViewManagers = [];
            //A tab is created for every element in the array,
            //i.e. for every concrete subset of the many property
            for (var i = 0; i < result.length; i++) {
                addTab(result[i], tumlUri, propertyNavigatingTo, {forLookup:false, forManyComponent:false, isOne:isOne, forCreation:forCreation});
            }
        }

        function addTab(result, tumlUri, propertyNavigatingTo, options) {
            var metaForData = result.meta.to;

            var tumlTabViewManager;
            if (options.isOne) {
                tumlTabViewManager = new Tuml.TumlTabOneViewManager(
                    {propertyNavigatingTo:propertyNavigatingTo,
                        many:!options.isOne,
                        one:options.isOne,
                        query:false,
                        forLookup:options.forLookup,
                        forManyComponent:options.forManyComponent,
                        forOneComponent:options.forOneComponent
                    }, tumlUri, result
                );
                tumlTabViewManager.onPutOneSuccess.subscribe(function (e, args) {
                    self.onPutOneSuccess.notify(args, e, self);
                });
                tumlTabViewManager.onPostOneSuccess.subscribe(function (e, args) {
                    self.onPostOneSuccess.notify(args, e, self);
                });
                tumlTabViewManager.onClickOneComponent.subscribe(function (e, args) {
                    //Get the meta data
                    $.ajax({
                        url:args.property.tumlMetaDataUri,
                        type:"GET",
                        dataType:"json",
                        contentType:"json",
                        success:function (metaDataResponse, textStatus, jqXHR) {
                            $('#tab-container').tabs('disableTab', tumlTabViewManager.tabTitle);
                            if (args.data !== null) {
                                metaDataResponse[0].data = args.data;
                            }
                            var tumlOneComponentTabViewManager = addTab(
                                metaDataResponse[0],
                                args.tumlUri,
                                args.property,
                                {forLookup:false, forManyComponent:false, forOneComponent:true, isOne:true, forCreation:true}
                            );
                            tumlTabViewManager.setProperty(args.property);
                            tumlOneComponentTabViewManager.setLinkedTumlTabViewManager(tumlTabViewManager);
                        },
                        error:function (jqXHR, textStatus, errorThrown) {
                            alert('error getting ' + property.tumlMetaDataUri + '\n textStatus: ' + textStatus + '\n errorThrown: ' + errorThrown)
                        }
                    });
                });
                tumlTabViewManager.onClickManyComponent.subscribe(function (e, args) {
                    //Get the meta data
                    $.ajax({
                        url:args.property.tumlMetaDataUri,
                        type:"GET",
                        dataType:"json",
                        contentType:"json",
                        success:function (metaDataResponse, textStatus, jqXHR) {
                            $('#tab-container').tabs('disableTab', tumlTabViewManager.tabTitle);
                            if (args.data !== null) {
                                metaDataResponse[0].data = args.data;
                            }
                            var tumlOneComponentTabViewManager = addTab(
                                metaDataResponse[0],
                                args.tumlUri,
                                args.property,
                                {forLookup:false, forManyComponent:true, isOne:false, forCreation:true}
                            );
                            tumlTabViewManager.setProperty(args.property);
                            tumlOneComponentTabViewManager.setLinkedTumlTabViewManager(tumlTabViewManager);
                        },
                        error:function (jqXHR, textStatus, errorThrown) {
                            alert('error getting ' + property.tumlMetaDataUri + '\n textStatus: ' + textStatus + '\n errorThrown: ' + errorThrown)
                        }
                    });
                });
            } else {
                tumlTabViewManager = new Tuml.TumlTabManyViewManager(
                    {propertyNavigatingTo:propertyNavigatingTo,
                        many:!options.isOne,
                        one:options.isOne,
                        query:false,
                        forLookup:options.forLookup,
                        forManyComponent:options.forManyComponent
                    }, tumlUri, result
                );
                tumlTabViewManager.onSelectButtonSuccess.subscribe(function (e, args) {
                    console.log('TumlManyViewManager onSelectButtonSuccess fired');
                    tumlTabViewManager.getLinkedTumlTabViewManager().addItems(args.items);
                    //Closing the tab fires closeTab event which removes the tumlTabViewManager from the array
                    $('#tab-container').tabs('close', args.tabName + " Select");
                    $('#' + args.tabName + "Lookup").remove();
                    tumlTabViewManager.getLinkedTumlTabViewManager().enableTab();
                });
                tumlTabViewManager.onSelectCancelButtonSuccess.subscribe(function (e, args) {
                    console.log('TumlManyViewManager onSelectCancelButtonSuccess fired');
                    //Closing the tab fires closeTab event which removes the tumlTabViewManager from the array
                    $('#tab-container').tabs('close', args.tabName + " Select");
                    $('#' + args.tabName + "Lookup").remove();
                    tumlTabViewManager.getLinkedTumlTabViewManager().enableTab();
                });
                tumlTabViewManager.onAddButtonSuccess.subscribe(function (e, args) {
                    console.log('TumlManyViewManager onAddButtonSuccess fired');
                    $('#tab-container').tabs('disableTab', metaForData.name);
                    var tumlLookupTabViewManager = addTab(
                        args.data,
                        args.tumlUri,
                        args.propertyNavigatingTo,
                        {forLookup:true, forManyComponent:false}
                    );
                    tumlLookupTabViewManager.setLinkedTumlTabViewManager(tumlTabViewManager);
                });
                tumlTabViewManager.onClickManyComponentCell.subscribe(function (e, args) {
                    console.log('TumlManyViewManager onClickManyComponentCell fired');
                    //Get the meta data
                    $.ajax({
                        url:args.property.tumlMetaDataUri,
                        type:"GET",
                        dataType:"json",
                        contentType:"json",
                        success:function (result, textStatus, jqXHR) {
                            $('#tab-container').tabs('disableTab', tumlTabViewManager.tabTitle);
                            result[0].data = args.data;
                            var tumlManyComponentTabViewManager = addTab(
                                result[0],
                                args.tumlUri,
                                args.property,
                                {forLookup:false, forManyComponent:true, forOneComponent:false, isOne:false, forCreation:true}
                            );
                            tumlTabViewManager.setCell(args.cell);
                            tumlManyComponentTabViewManager.setLinkedTumlTabViewManager(tumlTabViewManager);
                        },
                        error:function (jqXHR, textStatus, errorThrown) {
                            alert('error getting ' + property.tumlMetaDataUri + '\n textStatus: ' + textStatus + '\n errorThrown: ' + errorThrown)
                        }
                    });
                });

                tumlTabViewManager.onClickOneComponentCell.subscribe(function (e, args) {
                    console.log('TumlManyViewManager onClickOneComponentCell fired');
                    //Get the meta data
                    $.ajax({
                        url:args.property.tumlMetaDataUri,
                        type:"GET",
                        dataType:"json",
                        contentType:"json",
                        success:function (result, textStatus, jqXHR) {
                            $('#tab-container').tabs('disableTab', tumlTabViewManager.tabTitle);
                            if (args.data.length !== 0) {
                                result[0].data = args.data;
                            }
                            var tumlOneComponentTabViewManager = addTab(
                                result[0],
                                args.tumlUri,
                                args.property,
                                {forLookup:false, forManyComponent:false, forOneComponent:true, isOne:true, forCreation:true}
                            );
                            tumlTabViewManager.setCell(args.cell);
                            tumlOneComponentTabViewManager.setLinkedTumlTabViewManager(tumlTabViewManager);
                        },
                        error:function (jqXHR, textStatus, errorThrown) {
                            alert('error getting ' + property.tumlMetaDataUri + '\n textStatus: ' + textStatus + '\n errorThrown: ' + errorThrown)
                        }
                    });
                });

                tumlTabViewManager.onSelfCellClick.subscribe(function (e, args) {
                    console.log('TumlManyViewManager onSelfCellClick fired');
                    self.onSelfCellClick.notify(args, e, self);
                });
                tumlTabViewManager.onContextMenuClickLink.subscribe(function (e, args) {
                    console.log('TumlManyViewManager onContextMenuClickLink fired');
                    self.onContextMenuClickLink.notify(args, e, self);
                });
                tumlTabViewManager.onContextMenuClickDelete.subscribe(function (e, args) {
                    console.log('TumlManyViewManager onContextMenuClickDelete fired');
                    self.onContextMenuClickDelete.notify(args, e, self);
                });
            }

            tumlTabViewManager.onPutSuccess.subscribe(function (e, args) {
                console.log('TumlManyViewManager onPutSuccess fired');
                self.onPutSuccess.notify(args, e, self);
            });
            tumlTabViewManager.onPutFailure.subscribe(function (e, args) {
                console.log('TumlManyViewManager onPutFailure fired');
                self.onPutFailure.notify(args, e, self);
            });
            tumlTabViewManager.onPostSuccess.subscribe(function (e, args) {
                console.log('TumlManyViewManager onPostSuccess fired');
                self.onPostSuccess.notify(args, e, self);
            });
            tumlTabViewManager.onPostFailure.subscribe(function (e, args) {
                console.log('TumlManyViewManager onPostFailure fired');
                self.onPostFailure.notify(args, e, self);
            });
            tumlTabViewManager.onDeleteSuccess.subscribe(function (e, args) {
                console.log('TumlManyViewManager onDeleteSuccess fired');
                self.onDeleteSuccess.notify(args, e, self);
            });
            tumlTabViewManager.onDeleteFailure.subscribe(function (e, args) {
                console.log('TumlManyViewManager onDeleteFailure fired');
                self.onDeleteFailure.notify(args, e, self);
            });
            tumlTabViewManager.onCancel.subscribe(function (e, args) {
                console.log('TumlManyViewManager onCancel fired');
                self.onCancel.notify(args, e, self);
            });

            tumlTabViewManager.createTab();
            tumlTabViewManagers.push(tumlTabViewManager);

            //Create the grid
            if (!options.isOne) {
                tumlTabViewManager.createGrid(result);
            } else {
                tumlTabViewManager.createOne(result.data[0], metaForData, options.forCreation);
            }

            return tumlTabViewManager;

        }

        function closeTab(title, index) {
            tumlTabViewManagers[index].clear();
            tumlTabViewManagers.splice(index, 1);
        }

        function openQuery(tumlUri, oclExecuteUri, qualifiedName, tabDivName, queryEnum, queryString) {
            //Check is there is already a tab open for this query
            var tumlTabViewManagerQuery;
            var tabIndex = 0;
            for (var j = 0; j < tumlTabViewManagers.length; j++) {
                if (tumlTabViewManagers[j].oneManyOrQuery.query && tumlTabViewManagers[j].tabDivName == tabDivName) {
                    tumlTabViewManagerQuery = tumlTabViewManagers[j];
                    tabIndex = j;
                    break;
                }
            }
            if (tumlTabViewManagerQuery === undefined) {
                $('#tab-container').tabs('add', {title:tabDivName, content:'<div id="' + tabDivName + '" />', closable:true});
                var tumlTabViewManager = new Tuml.TumlTabQueryViewManager({many:false, one:false, query:true}, tumlUri, qualifiedName, tabDivName);
                tumlTabViewManagers.push(tumlTabViewManager);
                tumlTabViewManager.createQuery(oclExecuteUri, queryEnum, queryString);
            } else {
                //Just make the tab active
                $('#tab-container').tabs('select', tabIndex);
            }

        }

        function clear() {
            for (var i = 0; i < tumlTabViewManagers.length; i++) {
                tumlTabViewManagers[i].clear();
            }
            tumlTabViewManagers.length = 0;
        }

        //Public api
        $.extend(this, {
            "TumlManyViewManagerVersion":"1.0.0",
            //These events are propogated from the grid
            "onPutSuccess":new Tuml.Event(),
            "onPutFailure":new Tuml.Event(),
            "onPostSuccess":new Tuml.Event(),
            "onPostFailure":new Tuml.Event(),
            "onDeleteSuccess":new Tuml.Event(),
            "onDeleteFailure":new Tuml.Event(),
            "onCancel":new Tuml.Event(),
            "onSelfCellClick":new Tuml.Event(),
            "onContextMenuClickLink":new Tuml.Event(),
            "onContextMenuClickDelete":new Tuml.Event(),

            "onPutOneSuccess":new Tuml.Event(),
            "onPostOneSuccess":new Tuml.Event(),

            "refresh":refresh,
            "openQuery":openQuery,
            "closeTab":closeTab,
            "clear":clear
        });

        init();
    }

})(jQuery);
