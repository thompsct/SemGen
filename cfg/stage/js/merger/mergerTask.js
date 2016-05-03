/**
 * 
 */
//TODO: Save the current stage graph, clear it, and load relevant nodes of merge resolution.

MergerTask.prototype = new Task();
MergerTask.prototype.constructor = MergerTask;

function MergerTask(graph, state) {
	Task.prototype.constructor.call(this, graph, state);
	
	var merger = this;
	var nodes = this.nodes;
	var task = this;
	
	var resolutions = [];
	// Preview merge resolutions


	var t = document.querySelector('#mergerContent');

	var clone = document.importNode(t.content, true);
	document.querySelector('#modalContent').appendChild(clone);

	// Quit merger
	$("#quitMergerBtn").click(function() {
		// TODO: Warning dialog before quitting
		$("#activeTaskText").removeClass('blink');
		$("#mergerIcon").remove();
		sender.minimizeTask(task);
	})	
	
	//$('[data-toggle="tooltip"]').tooltip();
	
	//Create the resolution panel
	this.addResolutionPanel = function(overlap) {
		
		var dsradiobutton = function (nodeside, desc, id) {
			var nodetype = NodeTypeMap[desc.type];
			return '<label>' +
				'<div class="modelVarName">' + desc.name + '</div>' +
				//'<div class="modelVarEquation">' + desc.equation + '</div>' +
				//'<svg class="'+ nodeside + '" height="10" width="10">' +
				//'<circle cx="5" cy="5" r="5" fill="' + nodetype.color + '"/>' +
				//'</svg>' +
				'<input class="mergeResRadio" type="radio" name="mergeResRadio' + id + '">' +
				'</label>';

		};

		var t = document.querySelector('#overlapPanel');
		var clone = document.importNode(t.content, true);
		
		clone.id = 'res' + resolutions.length;
		clone.index = resolutions.length;
		
		clone.querySelector('#leftRes').innerHTML = dsradiobutton('leftNode', overlap.dsleft, clone.id);
		clone.querySelector('#rightRes').innerHTML = dsradiobutton('rightNode', overlap.dsright, clone.id);
		clone.querySelector('#ignoreRes').innerHTML = '<label><div class="ignoreLabel">Ignore</div><input class="mergeResRadio" type="radio" name="mergeResRadio' + clone.id + '"></label>';

		clone.querySelector('.collapsePane').setAttribute("href", "#collapsePane" + clone.id);
		clone.querySelector('.collapse').setAttribute("id", "collapsePane" + clone.id);
		
		clone.querySelector('.collapsePane').setAttribute("onclick", 'sender.requestPreview(' + clone.index + ');');
		
		resolutions.push(clone);
		document.querySelector('#modalContent #overlapPanels').appendChild(clone);
		
	}

	//Preview graphs
	this.leftgraph = new PreviewGraph("modelAStage");
	this.midgraph = new PreviewGraph("modelABStage");
	this.rightgraph = new PreviewGraph("modelBStage");
	
	var isResizing = false,
	lastDownY = 0;

	$(function () {
		var container = $('.modal-content'),
			top = $('.mergePreview'),
			bottom = $('.modal-body'),
			handle = $('#resizeHandle');
	
		handle.on('mousedown', function (e) {
			isResizing = true;
			lastDownY = e.clientY;
		});
	
		$(document).on('mousemove', function (e) {
			// we don't want to do anything if we aren't resizing.
			if (!isResizing)
				return;
	
			var percentage = (e.pageY / container.innerWidth) * 100;
			var mainPercentage = 100-percentage;
	
			top.css('height', percentage);
			bottom.css('height', mainPercentage);
		}).on('mouseup', function (e) {
			// stop resizing
			isResizing = false;
		});
	});
	
	receiver.onShowOverlaps(function(data) {
		data.forEach(function(d) {
			task.addResolutionPanel(d);	
		});
	});
	
	receiver.onShowPreview(function(data) {
		merger.leftgraph.update(data.left);
		merger.midgraph.update(data.middle);
		merger.rightgraph.update(data.right);
	});
}

MergerTask.prototype.onInitialize = function() {
	var nodearr = getSymbolArray(this.nodes);
	$(".leftModelName").append(nodearr[0].id);
	$(".rightModelName").append(nodearr[1].id);
	
	this.leftgraph.initialize();
	this.midgraph.initialize();
	this.rightgraph.initialize();
	
	if($("#mergerIcon").length == 0	) {
		$("#activeTaskPanel").append("<a data-toggle='modal' href='#taskModal'><img id='mergerIcon' src='../../src/semgen/icons/mergeicon2020.png' /></a>");
	}
	
	sender.requestOverlaps();
}

MergerTask.prototype.onModelSelection = function(node) {}

MergerTask.prototype.onClose = function() {}