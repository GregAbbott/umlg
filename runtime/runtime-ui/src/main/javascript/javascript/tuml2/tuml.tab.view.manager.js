(function ($) {
    // register namespace
    $.extend(true, window, {
        Tuml: {
            TumlBaseTabViewManager: TumlBaseTabViewManager,
            TumlTabOneViewManager: TumlTabOneViewManager,
            TumlTabManyComponentViewManager: TumlTabManyComponentViewManager,
            TumlTabManyViewManager: TumlTabManyViewManager,
            TumlTabQueryViewManager: TumlTabQueryViewManager
        }
    });

    function TumlBaseTabViewManager(tabEnum, tabContainer, tumlUri, result, propertyNavigatingTo) {

        this.TumlBaseTabViewManager = "1.0.0";

        this.backupData = null;

        this.tabEnum = tabEnum;
        //open is different to active, if there are nested tabs then a parent tab will be active but not open.
        //This is needed when repopulating the grids from the server in order to find the visible grid.
        this.open = false;

        function getTab(title) {
            return $('#tab-container').tabs('getTab', title);
        }

        this.disableTab = function () {
            $('#tab-container').tabs('disableTab', this.tabTitleName);
        }

        Tuml.TumlTabContainerManager.call(this, tabContainer, propertyNavigatingTo);

        if (this.result !== undefined) {
            this.init(tumlUri, result);
        }

        this.handleLookup = function (lookupUri, qualifiedName, loadDataCallback) {
            this.parentTabContainerManager.handleLookup(lookupUri, qualifiedName, loadDataCallback);
        }

    }

    TumlBaseTabViewManager.prototype = new Tuml.TumlTabContainerManager;

    TumlBaseTabViewManager.prototype.passOnValidationResults = function (tumlManyComponentTabViewManager, row, propertyName) {
        var componentValidationResultsToRemove = [];
        if (this.validationResults !== null) {
            for (var j = 0; j < this.validationResults.length; j++) {
                var validationResult = this.validationResults[j];
                if (validationResult.row == row && validationResult.property == propertyName) {
                    for (var k = 0; k < validationResult.validationResults.length; k++) {
                        var componentValidationResult = validationResult.validationResults[k];
                        if (componentValidationResult.validationResults.length > 0) {
                            tumlManyComponentTabViewManager.validationResults = validationResult.validationResults;
                        }

                    }
                    componentValidationResultsToRemove.push(j);
                }
            }
            for (var j = 0; j < componentValidationResultsToRemove.length; j++) {
                this.validationResults.splice(componentValidationResultsToRemove[j], 1);
            }
        }
    }

    TumlBaseTabViewManager.prototype.getTabId = function () {
        return this.tabId;
    }

    TumlBaseTabViewManager.prototype.validateUpdate = function () {
        throw 'TumlBaseTabViewManager.prototype.validateUpdate must be overriden';
    }

    TumlBaseTabViewManager.prototype.closeTab = function () {
        this.clearAllTabs();
        $("#" + this.tabId).remove();
        this.li.remove();
        var indexOftab = this.parentTabContainerManager.tumlTabViewManagers.indexOf(this);
        this.parentTabContainerManager.tumlTabViewManagers.splice(indexOftab, 1);
    }

    TumlBaseTabViewManager.prototype.saveAndCloseTab = function () {
        this.saveAndClearAllTabs();
        this.parentTabContainerManager.setCellValue(this.tumlTabGridManager.dataView.getItems());
        $("#" + this.tabId).remove();
        this.li.remove();
        var indexOfTab = this.parentTabContainerManager.tumlTabViewManagers.indexOf(this);
        this.parentTabContainerManager.tumlTabViewManagers.splice(indexOfTab, 1);
    }

    TumlBaseTabViewManager.prototype.init = function (tumlUri, result) {
        this.metaForData = result.meta;
    }

    TumlBaseTabViewManager.prototype.createTab = function () {
        var tabTemplate;
        if (this.parentTabContainerManager instanceof Tuml.TumlMainViewManager) {
            tabTemplate = "<li id='li" + this.tabId + "'><a href='#{href}'>#{label}</a>";
        } else {
            tabTemplate = "<li id='li" + this.tabId + "'><a href='#{href}'>#{label}</a>" +
//                "<span class='ui-icon ui-icon-cancel'>Cancel Tab</span>" +
//                "<span class='ui-icon ui-icon-plus'>Save Tab</span>" +
//                "<span class='ui-icon ui-icon-close'>Close Tab</span>" +
                "</li>";
        }
        var label = this.tabTitleName;
        var id = this.tabId;
        this.li = $(tabTemplate.replace(/#\{href\}/g, "#" + id).replace(/#\{label\}/g, label));

//        // cancel icon: refreshing the data
//        this.li.find("span.ui-icon-cancel").click(function () {
//            if (Slick.GlobalEditorLock.cancelCurrentEdit()) {
//                self.closeTab();
//            }
//        });
//        // save icon: save and close the tab on click
//        this.li.find("span.ui-icon-plus").click(function () {
//            if (Slick.GlobalEditorLock.commitCurrentEdit()) {
//                self.saveAndCloseTab();
//            }
//        });
//        // close icon: removing the tab on click
//        this.li.find("span.ui-icon-close").click(function () {
//            if (Slick.GlobalEditorLock.commitCurrentEdit()) {
//                self.closeTab();
//            }
//        });

        this.parentTabContainer.find(".ui-tabs-nav").append(this.li);
        var divPanel = $('<div />', {id: this.tabId});
        this.parentTabContainer.append(divPanel);
        $.data(divPanel[0], 'tabEnum', this.tabEnum);

        this.parentTabContainer.tabs("refresh");
        return divPanel;
    }

    TumlBaseTabViewManager.prototype.getTabData = function () {
        alert('This must be overriden');
    }

    TumlBaseTabViewManager.prototype.openManyComponent = function (data, cell, tumlUri, property) {
        throw 'TumlBaseTabViewManager.prototype.openManyComponent must be overriden';
    }

    TumlBaseTabViewManager.prototype.openOneComponent = function (data, cell, tumlUri, property) {
        throw 'TumlBaseTabViewManager.prototype.openOneComponent must be overriden';
    }

    TumlBaseTabViewManager.prototype.saveNewRow = function () {
        this.parentTabContainerManager.updateMultiplicityWarningHeader();
        //Save the child grids into the component's cell
        if (this.tumlTabViewManagers.length > 0) {

            var data = null;
            var tumlTabViewManager = this.tumlTabViewManagers[0];
            if (tumlTabViewManager instanceof TumlTabOneViewManager) {
                data = tumlTabViewManager.getTabData();
            } else {
                data = [];
                for (var i = 0; i < this.tumlTabViewManagers.length; i++) {
                    var tumlTabViewManager = this.tumlTabViewManagers[i];
                    data.push.apply(data, tumlTabViewManager.getTabData());
                }
            }

            this.setCellValue(data);
        }
        this.parentTabContainerManager.saveNewRow();
    }

    TumlBaseTabViewManager.prototype.updateGridAfterCommit = function (item) {
        for (var i = 0; i < this.tumlTabViewManagers.length; i++) {

            var tumlTabViewManager = this.tumlTabViewManagers[i];
            tumlTabViewManager.beginUpdate();
            var componentData = item[tumlTabViewManager.propertyNavigatingTo.name];

            if (tumlTabViewManager instanceof TumlTabOneViewManager) {
                tumlTabViewManager.updateGridAfterCommit(componentData);
            } else {
                for (var j = 0; j < componentData.length; j++) {
                    var gridRow = componentData[j];
                    if (tumlTabViewManager.metaForData.to.qualifiedName === gridRow.qualifiedName) {
                        tumlTabViewManager.updateGridAfterCommit(gridRow);
                    }
                }
            }

            tumlTabViewManager.endUpdate(true);
        }
    }

    TumlBaseTabViewManager.prototype.updateGridAfterRollback = function (item) {
        for (var i = 0; i < this.tumlTabViewManagers.length; i++) {

            var tumlTabViewManager = this.tumlTabViewManagers[i];
            tumlTabViewManager.beginUpdate();
            var componentData = item[tumlTabViewManager.propertyNavigatingTo.name];

            if (tumlTabViewManager instanceof TumlTabOneViewManager) {
                tumlTabViewManager.updateGridAfterRollback(componentData);
            } else {
                for (var j = 0; j < componentData.length; j++) {
                    var gridRow = componentData[j];
                    if (tumlTabViewManager.metaForData.to.qualifiedName === gridRow.qualifiedName) {
                        tumlTabViewManager.updateGridAfterRollback(gridRow);
                    }
                }
            }

            tumlTabViewManager.endUpdate(true);
        }
    }

    TumlBaseTabViewManager.prototype.beginUpdate = function () {
    }

    TumlBaseTabViewManager.prototype.endUpdate = function () {
    }

    function TumlTabQueryViewManager(tabEnum, tabContainer, instanceQueryUri, classQueryUri, tabDivName, tabTitleName, queryId) {

        this.TumlTabQueryViewManager = "1.0.0";

        if (queryId !== undefined) {
            this.queryId = queryId;
        }

        this.tabId = tabDivName;
        this.tabTitleName = tabTitleName;
        this.tumlTabQueryManager = new Tuml.TumlTabQueryManager(this, instanceQueryUri, classQueryUri, this.queryId);

        this.createQuery = function (oclExecuteUri, query, post) {
            this.tumlTabQueryManager.createQuery(this.tabId, oclExecuteUri, query, post);
        }

        this.afterSaveInstance = function (result) {
            var previousIndex = this.parentTabContainerManager.tumlTabViewManagers.indexOf(this);
            this.closeTab();
            this.parentTabContainerManager.afterSaveInstance(result, previousIndex);
        }

        this.afterUpdateInstance = function (result) {
            this.closeTab();
            this.parentTabContainerManager.afterUpdateInstance(result);
        }

        this.afterDeleteInstance = function (result) {
            var previousIndex = this.parentTabContainerManager.tumlTabViewManagers.indexOf(this);
            this.parentTabContainerManager.afterDeleteInstance(result);
            this.closeTab();
            this.parentTabContainer.tabs("option", "active", previousIndex - 1);
        }

        this.afterSaveClassQuery = function (result) {
            var previousIndex = this.parentTabContainerManager.tumlTabViewManagers.indexOf(this);
            this.closeTab();
            this.parentTabContainerManager.afterSaveClassQuery(result, previousIndex);
        }

        this.afterUpdateClassQuery = function (result) {
            this.closeTab();
            this.parentTabContainerManager.afterUpdateClassQuery(result);
        }

        this.afterDeleteClassQuery = function (result) {
            var previousIndex = this.parentTabContainerManager.tumlTabViewManagers.indexOf(this);
            this.parentTabContainerManager.afterDeleteClassQuery(result);
            this.closeTab();
            this.parentTabContainer.tabs("option", "active", previousIndex - 1);
        }

        TumlBaseTabViewManager.call(this, tabEnum, tabContainer);
    }

    TumlTabQueryViewManager.prototype = new Tuml.TumlBaseTabViewManager;

    TumlTabQueryViewManager.prototype.saveViaKeyPress = function() {
        if (this.open) {
            return true;
        } else {
            return TumlBaseTabViewManager.prototype.saveViaKeyPress.call(this);
        }
    }

    TumlTabQueryViewManager.prototype.cancelViaKeyPress = function() {
        if (this.open) {
            return true;
        } else {
            return TumlBaseTabViewManager.prototype.cancelViaKeyPress.call(this);
        }
    }

    TumlTabQueryViewManager.prototype.closeTab = function () {
        var previousIndex = this.parentTabContainerManager.tumlTabViewManagers.indexOf(this);
        var nextIndex = previousIndex - 1;
        TumlBaseTabViewManager.prototype.closeTab.call(this);
        this.parentTabContainer.tabs("option", "active", nextIndex);
        var currentTab = this.parentTabContainerManager.tumlTabViewManagers[nextIndex];
        if (currentTab instanceof TumlTabQueryViewManager) {
            this.parentTabContainerManager.refreshQueryMenuCss(currentTab.queryId);
        } else {
            this.parentTabContainerManager.refreshQueryMenuCss(-1, 0);
        }
    }

    TumlTabQueryViewManager.prototype.createTab = function (post) {
        var self = this;
        var tabTemplate;
        if (this.parentTabContainerManager instanceof Tuml.TumlMainViewManager) {
            tabTemplate = "<li id='li" + this.tabId + "'><a href='#{href}'>#{label}</a>";
        } else {
            if (post) {
                tabTemplate = "<li id='li" + this.tabId + "'><a href='#{href}'>#{label}</a></li>";
            } else {
                tabTemplate = "<li id='li" + this.tabId + "'><a href='#{href}'>#{label}</a><span class='ui-icon ui-icon-close'>Close Tab</span></li>";
            }
        }
        var label = this.tabTitleName;
        var id = this.tabId;
        this.li = $(tabTemplate.replace(/#\{href\}/g, "#" + id).replace(/#\{label\}/g, label));

        if (!post) {
            // close icon: removing the tab on click
            this.li.find("span.ui-icon-close").click(function () {
                if (Slick.GlobalEditorLock.commitCurrentEdit()) {
                    self.closeTab();
                }
            });
        }

        this.parentTabContainer.find(".ui-tabs-nav").append(this.li);
        var divPanel = $('<div />', {id: this.tabId});
        this.parentTabContainer.append(divPanel);
        $.data(divPanel[0], 'tabEnum', this.tabEnum);
        this.parentTabContainer.tabs("refresh");

        if (this.queryId !== undefined) {
            $.data(divPanel[0], 'queryId', this.queryId);
        }

        return divPanel;
    }

    function TumlTabOneViewManager(tabEnum, tabContainer, oneManyOrQuery, tumlUri, result, propertyNavigatingTo) {

        this.TumlTabOneViewManager = "1.0.0";

        this.oneManyOrQuery = oneManyOrQuery;
        this.result = result;

        this.setComponentProperty = function (property) {
            this.componentProperty = property;
        }

        this.setFocus = function() {
            this.li.focus();
        }

        TumlBaseTabViewManager.call(this, tabEnum, tabContainer, tumlUri, result, propertyNavigatingTo);
    }

    TumlTabOneViewManager.prototype = new Tuml.TumlBaseTabViewManager;

    TumlTabOneViewManager.prototype.saveViaKeyPress = function() {
        if (this.open) {
            this.parentTabContainerManager.saveTabs();
            return true;
        } else {
            return TumlBaseTabViewManager.prototype.saveViaKeyPress.call(this);
        }
    }

    TumlTabOneViewManager.prototype.cancelViaKeyPress = function() {
        if (this.open) {
            this.parentTabContainerManager.doCancel();
            return true;
        } else {
            return TumlBaseTabViewManager.prototype.cancelViaKeyPress.call(this);
        }
    }

    TumlTabOneViewManager.prototype.createTab = function (result, forCreation) {
        if (this.oneManyOrQuery.forOneComponent) {
            this.tabId = this.result.meta.to.name + "OneComponent";
            this.tabTitleName = this.result.meta.to.name;
        } else {
            this.tabId = this.result.meta.to.name;
            this.tabTitleName = this.result.meta.to.name;
        }
        TumlBaseTabViewManager.prototype.createTab.call(this);
        this.parentTabContainer.tabs("option", "active", this.parentTabContainerManager.tumlTabViewManagers.length - 1);
        this.createOne(result.data, forCreation);
        this.addButtonsToOne();
    }

    TumlTabOneViewManager.prototype.disableButtons = function () {
        var tabsNav = this.tabContainer.find('.ui-tabs-nav');
        tabsNav.find('#' + this.getTabId() + 'save').attr('disabled', 'disabled');
    }

    TumlTabOneViewManager.prototype.enableButtons = function () {
        var tabsNav = this.tabContainer.find('.ui-tabs-nav');
        tabsNav.find('#' + this.getTabId() + 'save').removeAttr('disabled');
    }

    /**
     * this is called which in turn calls onBlur which in turn synchronizes the current focused property to the data model
     */
    TumlTabOneViewManager.prototype.commitCurrentEdit = function () {
        this.tumlTabOneManager.commitCurrentEdit();
    }

    TumlTabOneViewManager.prototype.addButtonsToOne = function () {
        var self = this;
        //Remove container buttons
        var tabsNav = this.parentTabContainerManager.tabContainer.find('.ui-tabs-nav');
        tabsNav.find('#tabcontainer-button').remove();

        var tabDiv = this.parentTabContainerManager.tabContainer.find('#' + this.getTabId()).find('.oneUl');
        var tabsButtonDiv = $('<div />', {id: 'tabcontainer-button', class: 'one-tabs-button'}).appendTo(tabDiv);
        var saveButton = $('<button />', {id: this.getTabId() + 'save'}).text('Save').appendTo(tabsButtonDiv);
        saveButton.button().click(
            function (event) {
                if (self.tumlTabOneManager.commitCurrentEdit()) {
                    self.parentTabContainerManager.saveTabs();
                }
                event.preventDefault();
            }
        );
        var cancelButton = $('<button />', {id: this.getTabId() + 'save'}).text('Cancel').appendTo(tabsButtonDiv);
        cancelButton.button().click(function (event) {
            self.parentTabContainerManager.doCancel();
            event.preventDefault();
        });
    }

    TumlTabOneViewManager.prototype.doCancel = function () {
        Tuml.TumlTabContainerManager.prototype.doInternalCancel.call(this);
        $('#formDiv' + this.tabTitleName).show();
        this.open = true;
    }

    TumlTabOneViewManager.prototype.validateUpdate = function () {
        var validationResults = [];
        var item = this.getTabData();
        if (this.metaForData.qualifiedName == item.qualifiedName) {
            for (var k = 0; k < this.metaForData.to.properties.length; k++) {
                var property = this.metaForData.to.properties[k];
                if (!property.composite && !property.inverseComposite && property.lower > 0) {
                    if (property.upper === -1 || property.upper > 1) {
                        if (item[property.name].length === 0) {
                            var validationResult = new Tuml.ValidationResult(0, property.name);
                            validationResult.message = property.name + " is a required field!";
                            validationResults.push(validationResult);
                        }
                    } else {
                        if (item[property.name] == null) {
                            var validationResult = new Tuml.ValidationResult(0, property.name);
                            validationResult.message = property.name + " is a required field!";
                            validationResults.push(validationResult);
                        }
                    }
                }
            }
        }
        return validationResults;
    }

    TumlTabOneViewManager.prototype.validateInsert = function () {
        var validationResults = [];
        var item = this.getTabData();
        if (this.metaForData.qualifiedName == item.qualifiedName) {
            for (var k = 0; k < this.metaForData.to.properties.length; k++) {
                var property = this.metaForData.to.properties[k];
                if (!property.inverseComposite && property.lower > 0) {
                    if (property.upper === -1 || property.upper > 1) {
                        if (item[property.name] == undefined || item[property.name].length === 0) {
                            var validationResult = new Tuml.ValidationResult(0, property.name);
                            validationResult.row = 0;
                            validationResult.property = property;
                            validationResult.qualifiedName = property.qualifiedName;
                            validationResult.message = property.name + " is a required field!";
                            validationResults.push(validationResult);
                        }
                    } else {
                        if (item[property.name] == null) {
                            var validationResult = new Tuml.ValidationResult(0, property.name);
                            validationResult.row = 0;
                            validationResult.property = property;
                            validationResult.qualifiedName = property.qualifiedName;
                            validationResult.message = property.name + " is a required field!";
                            validationResults.push(validationResult);
                        }
                    }
                }
            }
        }
        if (this.validationResults != null) {
            for (var j = 0; j < this.validationResults.length; j++) {
                var componentValidationResult = this.validationResults[j];
                validationResults.push(componentValidationResult);
            }
        }
        return validationResults;
    }

    TumlTabOneViewManager.prototype.getTabData = function () {
        return this.tumlTabOneManager.data;
    }

    TumlTabOneViewManager.prototype.updateGridAfterCommit = function (item) {
        this.tumlTabOneManager.updateGridAfterCommitOrRollback(item);
        TumlBaseTabViewManager.prototype.updateGridAfterCommit.call(this, item);
        this.oneManyOrQuery.forCreation = false;
        this.tumlTabOneManager.isForCreation = false;
    }

    TumlTabOneViewManager.prototype.updateGridAfterRollback = function (item) {
        this.tumlTabOneManager.updateGridAfterCommitOrRollback(item);
        TumlBaseTabViewManager.prototype.updateGridAfterRollback.call(this, item);
    }

    TumlTabOneViewManager.prototype.init = function (tumlUri, result) {
        TumlBaseTabViewManager.prototype.init.call(this, tumlUri, result);
        if (this.oneManyOrQuery.forOneComponent) {
            this.tumlTabOneManager = new Tuml.TumlTabComponentOneManager(this);
        } else {
            this.tumlTabOneManager = new Tuml.TumlTabOneManager(this);
        }
    }

    TumlTabOneViewManager.prototype.addToCellValue = function (value) {
        this.tumlTabOneManager.addToCellValue(this.property, value);
    }

    //Must be created after tabs have been created, else things look pretty bad like...
    TumlTabOneViewManager.prototype.createOne = function (result, forCreation) {
        this.tumlTabOneManager.refresh(result, this.metaForData, forCreation);
    }

    TumlTabOneViewManager.prototype.setComponentProperty = function (property) {
        this.tumlTabOneManager.setComponentProperty(property);
    }

    TumlTabOneViewManager.prototype.setCellValue = function (value) {
        this.tumlTabOneManager.updateData(this.componentProperty, value);
        this.tumlTabOneManager.refreshFromDataModel();
    }

    TumlTabOneViewManager.prototype.openManyComponent = function (data, /*cell,*/ tumlUri, property) {
        var self = this;
        //Get the meta data.

        Tuml.Metadata.Cache.get(property.qualifiedName, property.tumlMetaDataUri,
            function (result) {
                var component = {meta: null, data: null};
                self.tabContainerProperty = property;
                var firstTumlManyComponentTabViewManager = null;
                for (var i = 0; i < result.length; i++) {
                    component.meta = result[i].meta;
                    self.maybeCreateTabContainer();
                    component.data = data;
                    var tumlManyComponentTabViewManager = self.createTabContainer(
                        tuml.tab.Enum.Properties,
                        component,
                        tumlUri,
                        {forLookup: false, forManyComponent: true, forOneComponent: false, isOne: false, forCreation: true},
                        property
                    );
                    self.setComponentProperty(property);
                    self.addToTumlTabViewManagers(tumlManyComponentTabViewManager);
                    tumlManyComponentTabViewManager.parentTabContainerManager = self;
                    $('#formDiv' + self.tabTitleName).hide();
                    tumlManyComponentTabViewManager.backupData = $.extend(true, [], data);
                    //pass through component's component validationResults
                    self.passOnValidationResults(tumlManyComponentTabViewManager, 0, self.tabContainerProperty.name);
                    tumlManyComponentTabViewManager.createTab(component, false);
                    if (i === 0) {
                        firstTumlManyComponentTabViewManager = tumlManyComponentTabViewManager;
                    }
                }
                self.open = false;
                //Set only the first tab to active
                if (firstTumlManyComponentTabViewManager !== null) {
                    self.tabContainer.tabs("option", "active", 0);
                }

                var qualifiedName = property.qualifiedName;
                self.updateNavigationHeader(qualifiedName);
                self.updateMultiplicityWarningHeader();
            }
        );
    }

    TumlTabOneViewManager.prototype.setValidationResults = function (qualifiedName, validationResults, count) {
        if (this.validationResults == null) {
            this.validationResults = [];
        }
        var componentValidationResult = new Tuml.ValidationResult(0, this.tabContainerProperty.name);
        componentValidationResult.qualifiedName = qualifiedName;
        componentValidationResult.validationResults = validationResults;
        componentValidationResult.multiplicity = count;
        this.validationResults.push(componentValidationResult);
    }

    TumlTabOneViewManager.prototype.openOneComponent = function (data, cell, tumlUri, property) {
        var self = this;
        //Get the meta data.
        Tuml.Metadata.Cache.get(property.qualifiedName, property.tumlMetaDataUri,
            function (result) {
                var component = {meta: null, data: null};
                self.tabContainerProperty = property;
                var firstTumlOneComponentTabViewManager = null;
                for (var i = 0; i < result.length; i++) {
                    component.meta = result[i].meta;
                    self.maybeCreateTabContainer();
                    component.data = data;
                    var tumlOneComponentTabViewManager = self.createTabContainer(
                        tuml.tab.Enum.Properties,
                        component,
                        tumlUri,
                        {forLookup: false, forManyComponent: false, forOneComponent: true, isOne: true, forCreation: true},
                        property
                    );
                    self.setCell(cell);
                    self.addToTumlTabViewManagers(tumlOneComponentTabViewManager);
                    tumlOneComponentTabViewManager.parentTabContainerManager = self;
                    $('#slickGrid' + self.tabId).hide();
                    tumlOneComponentTabViewManager.backupData = $.extend(true, {}, component.data);

                    if (component.data.id === undefined || component.data.id === null) {
                        //Create the object server side for ocl to execute...
//                        component.data.id = 'fake::' + Tuml.TumlFakeIndex++;
//                        component.data.tmpId = component.data.id;
                        tumlOneComponentTabViewManager.createTab(component, true);
                        self.saveNewRow();
                        if (tumlOneComponentTabViewManager.tumlTabOneManager.containsOneToOne) {
                            tumlOneComponentTabViewManager.parentTabContainerManager.addToOneToOneIndex(component.data.id, {});
                        }
                    } else {
                        tumlOneComponentTabViewManager.createTab(component, false);
                    }

                    if (i === 0) {
                        firstTumlOneComponentTabViewManager = tumlOneComponentTabViewManager;
                    }

                    self.passOnValidationResults(tumlOneComponentTabViewManager, 0, self.tabContainerProperty.name);

                }

                self.open = false;

                //Set only the first tab to active
                if (firstTumlOneComponentTabViewManager !== null) {
                    self.tabContainer.tabs("option", "active", 0);
                }

                var qualifiedName = property.qualifiedName;
                self.updateNavigationHeader(qualifiedName);
                self.updateMultiplicityWarningHeader();

            }
        );
    }

    TumlTabOneViewManager.prototype.saveTabs = function () {
        Tuml.TumlTabContainerManager.prototype.saveTabs.call(this);
        $('#formDiv' + this.tabTitleName).show();
        this.open = true;
    }

    function TumlTabManyViewManager(tabEnum, tabContainer, oneManyOrQuery, tumlUri, result, propertyNavigatingTo) {
        this.TumlTabManyViewManager = "1.0.0";
        this.oneManyOrQuery = oneManyOrQuery;
        this.result = result;
        this.tumlTabGridManager = null;
        TumlBaseTabViewManager.call(this, tabEnum, tabContainer, tumlUri, result, propertyNavigatingTo);

        this.setFocus = function(focusTo) {
            if (focusTo == Tuml.FocusEnum.CENTER_TAB) {
                this.li.focus();
            } else if (focusTo == Tuml.FocusEnum.CENTER_GRID) {
                this.tumlTabGridManager.setFocus();
            } else {
                throw Error('Can not call TumlTabManyViewManager.setFocus for ' + focusTo);
            }
        }

    }

    TumlTabManyViewManager.prototype = new Tuml.TumlBaseTabViewManager();

    TumlTabManyViewManager.prototype.doCancel = function () {
        this.doInternalCancel.call(this);
        this.open = true;
    }

    TumlTabManyViewManager.prototype.validateUpdate = function () {
        var validationResults = [];
        var toValidate = this.tumlTabGridManager.dataView.getUpdatedItems();
        for (var i = 0; i < toValidate.length; i++) {
            var item = toValidate[i];
            if (this.metaForData.qualifiedName == item.qualifiedName) {
                for (var k = 0; k < this.metaForData.to.properties.length; k++) {
                    var property = this.metaForData.to.properties[k];
                    if (!property.composite && !property.inverseComposite && property.lower > 0) {
                        if (property.upper === -1 || property.upper > 1) {
                            if (item[property.name].length === 0) {
                                var validationResult = new Tuml.ValidationResult(i, property.name);
                                validationResult.message = property.name + " is a required field!";
                                validationResults.push(validationResult);
                            }
                        } else {
                            if (item[property.name] == null) {
                                var validationResult = new Tuml.ValidationResult(i, property.name);
                                validationResult.message = property.name + " is a required field!";
                                validationResults.push(validationResult);
                            }
                        }
                    }
                }
            }
        }
        return validationResults;
    }

    TumlTabManyViewManager.prototype.validateInsert = function () {
        var validationResults = [];
        var toValidate = this.tumlTabGridManager.dataView.getNewItems();
        for (var row = 0; row < toValidate.length; row++) {
            var item = toValidate[row];
            if (this.metaForData.to.qualifiedName == item.qualifiedName) {
                for (var k = 0; k < this.metaForData.to.properties.length; k++) {
                    var property = this.metaForData.to.properties[k];

                    var validateRequired = false;
                    var validateAssociationClass = false;
                    if (property.associationClassOne) {
                        validateRequired = false;
                    } else if (property.memberEndOfAssociationClass && (property.inverseQualifiedName === this.propertyNavigatingTo.qualifiedName)) {
                        //Need to validate that the association class has been captured and is valid
                        validateRequired = true;
                        validateAssociationClass = true;
                    } else if (!property.inverseComposite && property.lower > 0) {
                        validateRequired = true;
                    }

                    if (validateRequired && !validateAssociationClass) {

                        this.validateInsertNonAssociationClass(property, item, row, validationResults);

                    } else if (validateAssociationClass) {

                        if (item[property.associationClassPropertyName] == null) {
                            var validationResult = new Tuml.ValidationResult(i, property.name);
                            validationResult.row = this.tumlTabGridManager.dataView.getIdxById(item.id);
                            validationResult.property = property;
                            validationResult.qualifiedName = property.qualifiedName;
                            validationResult.message = "The association class for " + property.name + " is required!";
                            validationResults.push(validationResult);
                        } else {

                            //Get the association class property
                            var associationClassProperty;
                            for (var l = 0; l < this.metaForData.to.properties.length; l++) {
                                associationClassProperty = this.metaForData.to.properties[l];
                                if (associationClassProperty.name === property.associationClassPropertyName) {
                                    break;
                                }
                            }
                            //Check that it has no already been validated, i.e. the association classes's ui has been
                            // opened and closed which would have executed the validation code.
                            var alreadyValidated = false;
                            if (this.validationResults != null) {
                                for (var m = 0; m < this.validationResults.length; m++) {
                                    var validationResult = this.validationResults[m];
                                    if (validationResult.property === associationClassProperty.name) {
                                        alreadyValidated = true;
                                        break;
                                    }
                                }
                            }

                            if (!alreadyValidated) {

                                var result = Tuml.Metadata.Cache.getFromCache(associationClassProperty.qualifiedName);
                                if (result === undefined) {
                                    throw 'Meta data for association class must be available in the cache!';
                                }
                                for (var k = 0; k < result.length; k++) {

                                    for (var l = 0; l < result[k].meta.to.properties.length; l++) {
                                        var associationClassPropertyProperty = result[k].meta.to.properties[l];
                                        //Do not validate the one back to the member end again
                                        if (!associationClassPropertyProperty.associationClassProperty && associationClassPropertyProperty.lower > 0) {
                                            this.validateInsertNonAssociationClass(associationClassPropertyProperty, item[associationClassProperty.name], row, validationResults);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (this.validationResults != null) {
            for (var j = 0; j < this.validationResults.length; j++) {
                var componentValidationResult = this.validationResults[j];
                validationResults.push(componentValidationResult);
            }
        }
        return validationResults;
    }

    TumlTabManyViewManager.prototype.validateInsertNonAssociationClass = function (property, item, row, validationResults) {
        if (property.upper === -1 || property.upper > 1) {
            if (item[property.name] == undefined || item[property.name].length === 0) {
                var validationResult = new Tuml.ValidationResult(row, property.name);
                validationResult.row = this.tumlTabGridManager.dataView.getIdxById(item.id);
                validationResult.property = property;
                validationResult.qualifiedName = property.qualifiedName;
                validationResult.message = property.name + " is a required field!";
                validationResults.push(validationResult);
            }
        } else if (!property.onePrimitive && !property.manyPrimitive && !property.oneEnumeration && property.dataTypeEnum == null && !property.composite && (property.manyToOne || property.oneToOne)) {

            if (item[property.name].id == null) {
                var validationResult = new Tuml.ValidationResult(row, property.name);
                validationResult.row = this.tumlTabGridManager.dataView.getIdxById(item.id);
                validationResult.property = property;
                validationResult.qualifiedName = property.qualifiedName;
                validationResult.message = property.name + " is a required field!";
                validationResults.push(validationResult);
            }

        } else {
            if (item[property.name] == null) {
                var validationResult = new Tuml.ValidationResult(row, property.name);
                validationResult.row = this.tumlTabGridManager.dataView.getIdxById(item.id);
                validationResult.property = property;
                validationResult.qualifiedName = property.qualifiedName;
                validationResult.message = property.name + " is a required field!";
                validationResults.push(validationResult);
            }
        }

    }

    TumlTabManyViewManager.prototype.updateOneForUpdate = function (id, displayName, fieldName, one) {
        var item = this.tumlTabGridManager.dataView.getItemById(id);
        if (item !== undefined) {
            item[fieldName].id = one.id;
            item[fieldName].displayName = displayName;
            this.tumlTabGridManager.dataView.updateItem(item.id, item, fieldName);
            this.tumlTabGridManager.grid.invalidateAllRows();
            this.tumlTabGridManager.grid.render();
        }
    }

    TumlTabManyViewManager.prototype.updateOne = function (fakeId, fieldName, one, indexForFakeId) {
        var item = this.tumlTabGridManager.dataView.getItemById(indexForFakeId.id);
        this.updateOneRecursive(item, fakeId, fieldName, one, indexForFakeId);
    }

    TumlTabManyViewManager.prototype.saveViaKeyPress = function() {
        if (this.open) {
            if (Slick.GlobalEditorLock.commitCurrentEdit()) {
                this.parentTabContainerManager.saveTabs();
                this.tumlTabGridManager.grid.focus();
            }
            return true;
        } else {
            return TumlBaseTabViewManager.prototype.saveViaKeyPress.call(this);
        }
    }

    TumlTabManyViewManager.prototype.cancelViaKeyPress = function() {
        if (this.open) {
            if (Slick.GlobalEditorLock.commitCurrentEdit()) {
                this.parentTabContainerManager.doCancel();
            }
            return true;
        } else {
            return TumlBaseTabViewManager.prototype.cancelViaKeyPress.call(this);
        }
    }

    TumlTabManyViewManager.prototype.updateOneRecursive = function (item, fakeId, fieldName, one, indexForFakeId) {

        var nextComponent = item[indexForFakeId.field];
        if (Array.isArray(nextComponent)) {

            for (var i = 0; i < nextComponent.length; i++) {

                var nextItem = nextComponent[i];
                if (nextItem.id === fakeId) {
                    nextItem[fieldName].id = one.id;
                    nextItem[fieldName].displayName = one.name;
                    console.log('set one to one to in array ' + nextItem[fieldName].id + ' ' + nextItem[fieldName].displayName);
                    return true;
                } else {
                    if (this.updateOneRecursive(nextItem, fakeId, fieldName, one, indexForFakeId.next)) {
                        return true;
                    }
                }

            }
            return false;

        } else if (typeof nextComponent === 'object') {

            var nextItem = nextComponent;
            if (nextItem.id === fakeId) {
                nextItem[fieldName].id = one.id;
                nextItem[fieldName].displayName = one.name;
                console.log('set one to one to from one ' + nextItem[fieldName].id + ' ' + nextItem[fieldName].displayName);
                return true;
            } else {
                return this.updateOneRecursive(nextItem, fakeId, fieldName, one, indexForFakeId.next);
            }

        }
        return false;
    }

    TumlTabManyViewManager.prototype.addToOneToOneIndex = function (fakeId, item) {
        var parentItem = this.tumlTabGridManager.dataView.getItem(this.componentCell.row);
        var field = this.tumlTabGridManager.columns[this.componentCell.cell].name;
        var newObject = {id: parentItem.id, field: field, next: item};
        this.parentTabContainerManager.addToOneToOneIndex(fakeId, newObject);
    }

    TumlTabManyViewManager.prototype.deactivateGrids = function () {
        this.tumlTabGridManager.active = false;
        Tuml.TumlTabContainerManager.prototype.deactivateGrids.call(this);
    }

    TumlTabManyViewManager.prototype.activeOpenTabsGrid = function () {
        if (this.tumlTabViewManagers.length === 0 && this.open === true) {
            this.tumlTabGridManager.active = true;
        }
        Tuml.TumlTabContainerManager.prototype.activeOpenTabsGrid.call(this);
    }

    TumlTabManyViewManager.prototype.updateGridAfterRollback = function (item) {
        this.tumlTabGridManager.updateGridAfterRollback(item);
        //Check if component tab is open for this particular item
        if (this.tumlTabViewManagers.length > 0) {

            //For new items there must be a tmpId
            if (item.tmpId !== undefined && this.tumlTabGridManager.dataView.getItem(this.componentCell.row).tmpId === item.tmpId) {
                TumlBaseTabViewManager.prototype.updateGridAfterRollback.call(this, item);
            } else if (this.tumlTabGridManager.dataView.getItem(this.componentCell.row).id === item.id) {
                //Updated items, currently this is only suppose to happen for association classes
                TumlBaseTabViewManager.prototype.updateGridAfterRollback.call(this, item);
            }

        }
    }

    TumlTabManyViewManager.prototype.updateGridAfterCommit = function (item) {
        this.tumlTabGridManager.updateGridAfterCommit(item);
        //Check if component tab is open for this particular item
        if (this.tumlTabViewManagers.length > 0) {
            if (this.tumlTabGridManager.dataView.getItem(this.componentCell.row).tmpId === item.tmpId) {
                TumlBaseTabViewManager.prototype.updateGridAfterCommit.call(this, item);
            }
        }
    }

    TumlTabManyViewManager.prototype.clearArraysAfterCommit = function () {
        this.tumlTabGridManager.clearArraysAfterCommit();
    }

    TumlTabManyViewManager.prototype.getTabData = function () {
        if (this.tumlTabGridManager.dataView.getItems().length > 0) {
            return this.tumlTabGridManager.dataView.getItems();
        } else {
            return [];
        }
    }

    TumlTabManyViewManager.prototype.beginUpdate = function () {
        this.tumlTabGridManager.beginUpdate();
    }

    TumlTabManyViewManager.prototype.endUpdate = function (editActiveCell) {
        this.tumlTabGridManager.endUpdate(editActiveCell);
    }

    TumlTabManyViewManager.prototype.handleDeleteRow = function () {
        this.parentTabContainerManager.updateMultiplicityWarningHeader();
        //Save the child grids into the component's cell
        if (this.tumlTabViewManagers.length > 0) {
            var data = [];
            for (var i = 0; i < this.tumlTabViewManagers.length; i++) {
                var tumlTabViewManager = this.tumlTabViewManagers[i];
                if (tumlTabViewManager.tumlTabGridManager.dataView.getItems().length > 0) {
                    data.push.apply(data, tumlTabViewManager.tumlTabGridManager.dataView.getItems());
                }
            }
            this.setCellValue(data);
        }
        this.parentTabContainerManager.handleDeleteRow();
    }

    TumlTabManyViewManager.prototype.createTab = function (result, forCreation) {
        if (this.oneManyOrQuery.forLookup) {
            this.tabId = this.result.meta.to.name + "Lookup";
            this.tabTitleName = this.result.meta.to.name + " Select";
        } else if (this.oneManyOrQuery.forManyComponent) {
            this.tabId = this.result.meta.to.name + "ManyComponent";
            this.tabTitleName = this.result.meta.to.name;
        } else {
            this.tabId = this.result.meta.to.name;
            this.tabTitleName = this.result.meta.to.name;
        }
        TumlBaseTabViewManager.prototype.createTab.call(this, result, forCreation);

        if (this.oneManyOrQuery.forLookup) {
            //Change the save button's label to select
            var tabsNav = this.parentTabContainer.find('.ui-tabs-nav');
            var tabContainerButton = tabsNav.find('#tabcontainer-button');
            var saveButton = tabContainerButton.find('#' + this.parentTabContainerManager.getTabId() + 'save');
            saveButton.find('span').text('Select');
        }

        this.parentTabContainer.tabs("option", "active", this.parentTabContainerManager.tumlTabViewManagers.length - 1);
        this.createGrid(result);
    }

    TumlTabManyViewManager.prototype.setCell = function (cell) {
        this.componentCell = cell;
    }

    TumlTabManyViewManager.prototype.init = function (tumlUri, result) {
        TumlBaseTabViewManager.prototype.init.call(this, tumlUri, result);
        if (this.oneManyOrQuery.forLookup) {
            this.tumlTabGridManager = new Tuml.TumlForManyLookupGridManager(this, tumlUri, this.propertyNavigatingTo);
        } else if (this.oneManyOrQuery.forManyComponent) {
            this.tumlTabGridManager = new Tuml.TumlManyComponentGridManager(this, tumlUri, this.propertyNavigatingTo);
        } else {
            this.tumlTabGridManager = new Tuml.TumlTabGridManager(this, tumlUri, this.propertyNavigatingTo);
        }
    }

    TumlTabManyViewManager.prototype.addItems = function (items) {
        this.tumlTabGridManager.addItems(items);
    }

    TumlTabManyViewManager.prototype.setCellValue = function (value) {
        this.tumlTabGridManager.setCellValue(this.componentCell, value);
    }

    TumlTabManyViewManager.prototype.createGrid = function (result) {
        //Create an extra div, not displaying nice directly in the tab panel
        var gridDiv = $('<div />', {id: 'slickGrid' + this.tabId, class: 'slickGridOuter'});
        $('#' + this.tabId).append(gridDiv);
        this.tumlTabGridManager.refresh(result, gridDiv);
    }

    TumlTabManyViewManager.prototype.openManyComponent = function (data, cell, tumlUri, property) {
        var self = this;
        //Get the meta data.
        Tuml.Metadata.Cache.get(property.qualifiedName, property.tumlMetaDataUri,
            function (result) {
                self.tabContainerProperty = property;
                var firstTumlManyComponentTabViewManager = null;
                for (var i = 0; i < result.length; i++) {

                    var component = {meta: result[i].meta, data: []};
                    self.maybeCreateTabContainer();

                    //Need to filter out the data per concrete class as returned in the meta data
                    for (var j = 0; j < data.length; j++) {
                        if (data[j].qualifiedName === component.meta.to.qualifiedName) {
                            component.data.push(data[j]);
                        }
                    }
                    var tumlManyComponentTabViewManager = self.createTabContainer(
                        tuml.tab.Enum.Properties,
                        component,
                        tumlUri,
                        {forLookup: false, forManyComponent: true, forOneComponent: false, isOne: false, forCreation: true},
                        property
                    );
                    self.setCell(cell);
                    self.addToTumlTabViewManagers(tumlManyComponentTabViewManager);
                    tumlManyComponentTabViewManager.parentTabContainerManager = self;
                    $('#slickGrid' + self.tabId).hide();
                    tumlManyComponentTabViewManager.backupData = $.extend(true, [], data);
                    //pass through component's component validationResults
                    self.passOnValidationResults(tumlManyComponentTabViewManager, cell.row, self.tumlTabGridManager.grid.getColumns()[cell.cell].name);
                    tumlManyComponentTabViewManager.createTab(component, false);
                    if (i === 0) {
                        firstTumlManyComponentTabViewManager = tumlManyComponentTabViewManager;
                    }
                }
                self.open = false;
                //Set only the first tab to active
                if (firstTumlManyComponentTabViewManager !== null) {
                    self.tabContainer.tabs("option", "active", 0);
                }
                var qualifiedName = property.qualifiedName;
                self.updateNavigationHeader(qualifiedName);
                self.updateMultiplicityWarningHeader();
            }
        );
    }

    TumlTabManyViewManager.prototype.openNonCompositeMany = function (property, qualifiedName) {
        var self = this;

        var contextvertexId = this.parentTabContainerManager.contextVertexId;
        var adjustedUri = property.tumlLookupUri.replace(new RegExp("\{(\s*?.*?)*?\}", 'gi'), contextvertexId);

        this.handleLookup(adjustedUri, qualifiedName,

            function (data) {
                for (var j = 0; j < data.length; j++) {
                    var concreteData = data[j];
                    var component = {meta: concreteData.meta, data: concreteData.data};
                    var firstTumlManyComponentTabViewManager = null;
                    self.maybeCreateTabContainer();
                    var tumlManyComponentTabViewManager = self.createTabContainer(
                        tuml.tab.Enum.Properties,
                        component,
                        property.tumlUri,
                        {forLookup: true, forManyComponent: false, forOneComponent: false, isOne: false, forCreation: false},
                        property
                    );
                    //                    self.setCell(cell);
                    self.addToTumlTabViewManagers(tumlManyComponentTabViewManager);
                    tumlManyComponentTabViewManager.parentTabContainerManager = self;
                    $('#slickGrid' + self.tabId).hide();
                    //                    tumlManyComponentTabViewManager.backupData = $.extend(true, [], data);
                    //pass through component's component validationResults
                    //                    self.passOnValidationResults(tumlManyComponentTabViewManager, cell.row, self.tumlTabGridManager.grid.getColumns()[cell.cell].name);
                    tumlManyComponentTabViewManager.createTab(component, false);
                    if (j === 0) {
                        firstTumlManyComponentTabViewManager = tumlManyComponentTabViewManager;
                    }
                }
                self.open = false;
                //Set only the first tab to active
                if (firstTumlManyComponentTabViewManager !== null) {
                    self.tabContainer.tabs("option", "active", 0);
                }
                var qualifiedName = property.qualifiedName;
                self.updateNavigationHeader(qualifiedName);
            }
        );
    }

    TumlTabManyViewManager.prototype.setValidationResults = function (qualifiedName, validationResults, count) {
        if (this.validationResults == null) {
            this.validationResults = [];
        }
        var componentValidationResult = new Tuml.ValidationResult(this.componentCell.row, this.tabContainerProperty.name);
        componentValidationResult.qualifiedName = qualifiedName;
        componentValidationResult.validationResults = validationResults;
        componentValidationResult.multiplicity = count;
        this.validationResults.push(componentValidationResult);
    }

    TumlTabManyViewManager.prototype.openOneComponent = function (data, cell, tumlUri, property) {
        var self = this;
        //Get the meta data.

        Tuml.Metadata.Cache.get(property.qualifiedName, property.tumlMetaDataUri,
            function (result) {
                var oneComponent = {meta: null, data: null};
                self.tabContainerProperty = property;
                var firstTumlOneComponentTabViewManager = null;
                for (var i = 0; i < result.length; i++) {
                    oneComponent.meta = result[i].meta
                    self.maybeCreateTabContainer();
                    oneComponent.data = data;
                    var tumlOneComponentTabViewManager = self.createTabContainer(
                        tuml.tab.Enum.Properties,
                        oneComponent,
                        tumlUri,
                        {forLookup: false, forManyComponent: false, forOneComponent: true, isOne: true, forCreation: true},
                        property
                    );
                    self.setCell(cell);
                    self.addToTumlTabViewManagers(tumlOneComponentTabViewManager);
                    tumlOneComponentTabViewManager.parentTabContainerManager = self;
                    $('#slickGrid' + self.tabId).hide();
                    tumlOneComponentTabViewManager.backupData = $.extend(true, {}, oneComponent.data);

                    if (oneComponent.data.id === undefined || oneComponent.data.id === null) {
                        //Create the object server side for ocl to execute...
                        oneComponent.data.id = 'fake::' + Tuml.TumlFakeIndex++;
                        oneComponent.data.tmpId = oneComponent.data.id;
                        tumlOneComponentTabViewManager.createTab(oneComponent, true);
                        tumlOneComponentTabViewManager.saveNewRow();
                        if (tumlOneComponentTabViewManager.tumlTabOneManager.containsOneToOne) {
                            tumlOneComponentTabViewManager.parentTabContainerManager.addToOneToOneIndex(oneComponent.data.id, {});
                        }
                    } else {
                        tumlOneComponentTabViewManager.createTab(oneComponent, oneComponent.data.id.indexOf('fake') !== -1);
                        tumlOneComponentTabViewManager.tumlTabOneManager.refreshFromDataModel();
                    }

                    if (i === 0) {
                        firstTumlOneComponentTabViewManager = tumlOneComponentTabViewManager;
                    }

                    //pass through component's component validationResults
                    self.passOnValidationResults(tumlOneComponentTabViewManager, cell.row, self.tumlTabGridManager.grid.getColumns()[cell.cell].name);

                }

                self.open = false;

                //Set only the first tab to active
                if (firstTumlOneComponentTabViewManager !== null) {
                    self.tabContainer.tabs("option", "active", 0);
                }

//                var qualifiedName = result[0].meta.qualifiedName;
                var qualifiedName = property.qualifiedName;
                self.updateNavigationHeader(qualifiedName);
                self.updateMultiplicityWarningHeader();

            }
        );
    }

    TumlTabManyViewManager.prototype.openAssociationClass = function (data, cell, tumlUri, property) {
        var self = this;
        //Get the meta data.

        Tuml.Metadata.Cache.get(property.qualifiedName, property.tumlMetaDataUri,
            function (result) {
                var oneComponent = {meta: null, data: null};
                self.tabContainerProperty = property;
                var firstTumlOneComponentTabViewManager = null;
                for (var i = 0; i < result.length; i++) {
                    oneComponent.meta = result[i].meta
                    self.maybeCreateTabContainer();
                    oneComponent.data = data;
                    var tumlOneComponentTabViewManager = self.createTabContainer(
                        tuml.tab.Enum.Properties,
                        oneComponent,
                        tumlUri,
                        {forLookup: false, forManyComponent: false, forOneComponent: true, isOne: true, forCreation: true},
                        property
                    );
                    self.setCell(cell);
                    self.addToTumlTabViewManagers(tumlOneComponentTabViewManager);
                    tumlOneComponentTabViewManager.parentTabContainerManager = self;
                    $('#slickGrid' + self.tabId).hide();
                    tumlOneComponentTabViewManager.backupData = $.extend(true, {}, oneComponent.data);

                    if (oneComponent.data.refreshFromDb !== undefined && oneComponent.data.refreshFromDb) {
                        //Create the object server side for ocl to execute...
                        tumlOneComponentTabViewManager.createTab(oneComponent, true);
                        tumlOneComponentTabViewManager.saveNewRow();
                        if (tumlOneComponentTabViewManager.tumlTabOneManager.containsOneToOne) {
                            tumlOneComponentTabViewManager.parentTabContainerManager.addToOneToOneIndex(oneComponent.data.id, {});
                        }
                    } else {
                        tumlOneComponentTabViewManager.createTab(oneComponent, oneComponent.data.id.indexOf('fake') !== -1);
                        tumlOneComponentTabViewManager.tumlTabOneManager.refreshFromDataModel();
                    }

                    if (i === 0) {
                        firstTumlOneComponentTabViewManager = tumlOneComponentTabViewManager;
                    }

                    //pass through component's component validationResults
                    self.passOnValidationResults(tumlOneComponentTabViewManager, cell.row, self.tumlTabGridManager.grid.getColumns()[cell.cell].name);

                }

                self.open = false;

                //Set only the first tab to active
                if (firstTumlOneComponentTabViewManager !== null) {
                    self.tabContainer.tabs("option", "active", 0);
                }

                var qualifiedName = property.qualifiedName;
                self.updateNavigationHeader(qualifiedName);
                self.updateMultiplicityWarningHeader();

            }
        );
    }

    TumlTabManyViewManager.prototype.saveTabs = function () {
        Tuml.TumlTabContainerManager.prototype.saveTabs.call(this);
        this.tumlTabGridManager.active = true;
        $('#slickGrid' + this.tabId).show();
        this.open = true;
    }

    function TumlTabManyComponentViewManager(tabEnum, tabContainer, oneManyOrQuery, tumlUri, result, propertyNavigatingTo) {

        this.TumlTabManyComponentViewManager = "1.0.0";

        TumlTabManyViewManager.call(this, tabEnum, tabContainer, oneManyOrQuery, tumlUri, result, propertyNavigatingTo);
    }

    TumlTabManyComponentViewManager.prototype = new Tuml.TumlTabManyViewManager();

    TumlTabManyComponentViewManager.prototype.closeTab = function () {
        //Save the many component's data into the parent tabs row's cell
        Slick.GlobalEditorLock.cancelCurrentEdit();
        TumlBaseTabViewManager.prototype.closeTab.call(this);
    }

})
    (jQuery);