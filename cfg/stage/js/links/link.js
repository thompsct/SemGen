/**
 * Represents a link in the d3 graph
 */
function Link(graph, srclink, output, input, length, fade, interSubmodelLink) {
	if(!graph)
		return;

	this.graph = graph;
	this.srclink = srclink;
	this.id = srclink.id;
	this.source = input;
	this.target = output;
	this.length = length;
	this.bidirectional = false;
	//this.value = 1;
	this.hidden = false;
	this.userCanHide = false;
	this.linkType = NodeTypeArray[srclink.linkType];
	this.fade = fade;
	this.inter = interSubmodelLink;

	this.arrowHeadWidth = (this.linkType == NodeType.MEDIATOR || this.srclink.linklevel == 2 || this.srclink.linklevel == 3) ? 0 : 3;
	
	this.getLinkLevel = function() {
		return this.srclink.linklevel;
	}
	
	this.linksNodes = function(innode, outnode) {
		return this.source==innode && this.target == outnode;
	}
	
	this.drawArrow = function(dx, dy, source, target) {
		if (this.arrowHeadWidth == 0)
			return "";
		
		var arrowHeadWidth = this.arrowHeadWidth;
		var theta = Math.atan2(dy, dx) + Math.PI * 2,
	    d90 = Math.PI / 2,
	    dtxs = target.xpos() - target.r * Math.cos(theta),
	    dtys = target.ypos() - target.r * Math.sin(theta),
	    cth = 10*Math.cos(theta),
	    sth = 10*Math.sin(theta),
	    c90th = Math.cos(d90 - theta),
	    s90th = Math.sin(d90 - theta);
		
		return 	"M" + dtxs + "," + dtys + "l" + (arrowHeadWidth * c90th - cth) + "," + (-arrowHeadWidth * s90th - sth) +
				"L" + (dtxs - arrowHeadWidth * c90th - cth) + "," + (dtys + arrowHeadWidth * s90th - sth);
	}
	
	this.draw = function (source, target) {
		var dx = target.xpos() - source.xpos(),
	    dy = target.ypos() - source.ypos(),
	    dr = 0,
	    arrow = this.drawArrow(dx, dy, source, target),
	    biarrow = this.bidirectional ? 
	    		this.drawArrow(source.xpos() - target.xpos(), source.ypos() - target.ypos(), target, source) : "";

		return "M" + source.xpos() + "," + source.ypos() +
				"A" + dr + "," + dr + " 0 0 1," + target.xpos() + "," + target.ypos() +
				"A" + dr + "," + dr + " 0 0 0," + source.xpos() + "," + source.ypos() +
				arrow + biarrow +
				"z";
	}

}

Link.prototype.createVisualElement = function (element, graph) {
	this.rootElement = d3.select(element);

	this.rootElement
			.attr("id", this.id)
			.attr("class", this.className);
			//.attr("class", "link")
	this.rootElement.append("svg:path")
			.attr("class", "link");
	
	//Check if link is inter-submodel
	if (this.srclink.linklevel != 0) {
		if(this.source.nodeType.id == 1 || this.target.nodeType.id == 1) {
			this.rootElement.select("path")
				.attr("class", "link interSub");
		}
	}
	if (this.inter) {
        this.rootElement.select("path")
            .attr("class", "link interSub");
	}

	//Intermodel link
	if (this.srclink.linklevel == 2) {
		this.rootElement.select("path")
			.attr("class", "intermodel")
			.attr("stroke-width", "3")
			.attr("stroke", "#5bc0de")
			.attr("stroke-opacity", .8);
	}

	//Custom link
	if (this.srclink.linklevel == 3) {
		this.rootElement.select("path")
			.attr("class", "custom")
			.attr("stroke-width", "3")
            .attr("stroke", "#f0ad4e")
            .attr("stroke-opacity", .8);
	}
	
	//Mediator
	if (this.linkType == NodeType.MEDIATOR) {
		this.rootElement.select("path")
			.attr("class", "link mediator");
	}
	if (this.fade) {
		this.rootElement.attr("opacity", "0.5");
	}
}

Link.prototype.tickHandler = function (element, graph) {
	var link = this;
	// Display and update links

	this.rootElement.select("path").attr("d", link.draw(this.source, this.target));
}

Link.prototype.getKeyInfo = function () {
	return {
		linkType: this.linkType,
		linkLevel: this.getLinkLevel(),
		canShowHide: this.userCanHide,
	};
}