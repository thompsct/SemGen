/**
 * Defines graph functionality
 *
 * Adapted from: http://stackoverflow.com/questions/11400241/updating-links-on-a-force-directed-graph-from-dynamic-json-data
 */


function Graph() {
	var graph = this;
	this.w = $(window).width();
	this.h = $(window).height();
	
		var visibleNodes = [];

	var svg = d3.select("#stage")
	    .append("svg")	    
	    .attr("id", "svg")
	    .attr("pointer-events", "all")
	    .attr("perserveAspectRatio", "xMinYMid");

	var vis = svg.append('g').attr("class", "canvas");
	
	this.force = d3.forceSimulation()
	.velocityDecay(0.5)
	.force("charge", d3.forceManyBody()
			.strength(function(d) {return d.charge;})
			.distanceMax(400)
			.theta(0.6))
	.force("link", d3.forceLink()
		.id(function(d) { return d.id; })
		.distance(function (d) 
				{ return d.length; })
				.strength(0.1)
	);
		
	var links = this.force.force("link").links(); 

	this.nodecharge = defaultcharge;
	this.linklength = defaultlinklength;
	this.color = d3.scaleOrdinal(d3.schemeCategory10);

	this.fixedMode = false;
		//Node type visibility: model, submodel, state, rate, constitutive, entity, process, mediator, null
	this.nodesVisible = [true, true, true, true, false, true, true, true, true];
	this.showorphans = false;
	
	this.depBehaviors = [];
	
	this.active = true;
	var nodes;

	this.setTaskNodes = function(tasknodes) {
		nodes = tasknodes;
		graph.update();
	};

	this.getVisibleNodes = function() {
		return visibleNodes;
	}

	this.getModels = function() {
		return getSymbolArray(nodes);
	}

	// Remove a node from the graph
	this.removeNode = function (node) {
		var i = 0;

	    while (i < links.length) {
	    	if ((links[i].source == node)||(links[i].target == node))
	            links.splice(i,1);
	        else
	        	i++;
	    }
	    graph.update();
	    $(this).triggerHandler("nodeRemoved", [node]);
	};

	// Remove all links
	this.removeAllLinks = function(){
	    links.length = 0;
	};

	// Check to see if there's at least 1 node of the given type
	this.hasNodeOfType = function (type) {
		for(var index = 0; index < visibleNodes.length; index++) {
			if(visibleNodes[index].nodeType == type)
				return true;
		}

		return false;
	};

	// Hide all visibleNodes of the given type
	this.showNodes = function (type) {
		this.nodesVisible[NodeTypeMap[type].id] = true;
		this.update();
	}

	// Hide all visibleNodes of the given type
	this.hideNodes = function (type) {
		this.nodesVisible[NodeTypeMap[type].id] = false;
		this.update();
	}

	/**
	 * Updates the graph
	 */
	var path;
	var node;
	this.update = function () {
		$(this).triggerHandler("preupdate");

		bruteForceRefresh.call(this);

		// Add the links
		path = vis.selectAll(".link")
			.data(links, function(d) { return d.id; });

		path.enter().append("g")
        	.each(function (d) { d.createVisualElement(this, graph); });

		path.exit().remove();

		// Build the visibleNodes
	    node = vis.selectAll(".node")
	        .data(visibleNodes, function(d) { return d.id; });

	    node.enter().append("g")
	        .each(function (d) { d.createVisualElement(this, graph); });

	   node.exit().remove();
	    
	    // Define the tick function
	    this.force
	    	.nodes(visibleNodes)
	    	.on("tick", this.tick)
	    	.alphaTarget(1)
	    	.restart();

	    $(this).triggerHandler("postupdate");
	    
	    if (this.fixedMode) {
	    	setTimeout(function() {
	    		this.pause();
	    	}, 7000);
	    }
	};

	// Brute force redraw
	// Motivation:
	//	The z-index in SVG relies on the order of elements in html.
	//	The way things are added using D3, we don't have much control
	//	over ordering when we're adding and removing element dynamically.
	//	So to get control back we remove everything and redraw everything from scratch
	var refreshing = false;
	var bruteForceRefresh = function () {
		if(refreshing)
			return;

		refreshing = true;
		//Remove all graph objects
		vis.selectAll("*").remove();
		// Remove all visibleNodes from the graph
		visibleNodes.length = 0;

		// Remove all links from the graph
		this.removeAllLinks();

		// Add the visibleNodes back
		for (var x in nodes) {
			nodes[x].globalApplyUntilTrue(function(d) {
				if (d.isVisible()) {
					visibleNodes.push(d);
				}
				if (!d.canlink && !d.showchildren) return true;
				var nodelinks = d.getLinks(links);
				if (nodelinks) {
					nodelinks.forEach(function(l) {
						links.push(l);
					});
				}
				else {
					d.charge = d.defaultcharge/3;
				}
				return false;
			});
		}

		refreshing = false;
	}


	this.tick = function () {
		// Execute the tick handler for each link
		path.enter().each(function (d) {
			d.tickHandler(this, graph);
		})

    	// Execute the tick handler for each node
    	node.enter().each(function (d) {
    		d.tickHandler(this, graph);
    	});
	};

	// Find a node by its id
	this.findNode = function(id) {
		var nodewithid = null;
		for (var x in nodes) {
			nodes[x].globalApplyUntilTrue(function(n) {
				if (n.id == id) {
					nodewithid = n;
					return true;
				}
				return false;
			});
		}
	   return nodewithid;
	};
	
	// Find a visible node by its id
	this.findVisibleNode = function(id) {
	    for (var i in visibleNodes) {
	        if (visibleNodes[i].id === id)
	        	return visibleNodes[i];
	    }
	};

	// Find a link by its id
	this.findLink = function(id) {
		for (var i in links) {
			if (links[i].id === id)
				return links[i];
		}
	}

	// Highlight a node, its links, link labels, and the visibleNodes that its linked to
	this.highlightMode = function (highlightNode) {
		// Remove any existing dim assignments
		vis.selectAll(".node, .link").each(function (d) {
			d3.select(this).classed("dim", false);

			// Replace label with display name
			d3.select(this).selectAll("text")
				.text(function(d) {
					return d.displayName;
				})
		});

		// If no node was passed in there's nothing to dim
		if(!highlightNode)
			return;

		// Get all the visibleNodes that need to be highlighted
		// and dim all the links and labels that need to be dimmed
		var nodesToHighlight = {};
		nodesToHighlight[highlightNode.index] = 1;
		vis.selectAll(".link").each(function (d) {
			if(d.source == highlightNode || d.target == highlightNode) {
				nodesToHighlight[d.source.index] = 1;
				nodesToHighlight[d.target.index] = 1;

				// Display full name for highlighted links
				d3.select(this).selectAll("text")
					.text(function(d) {
						return d.name;
					})

				return;
			}

			// Dim the link since it's not connected to the node we care about
			d3.select(this).classed("dim", true);
		});

		// Dim all the visibleNodes that need to be dimmed
		vis.selectAll(".node").each(function (d) {
			if(!nodesToHighlight[d.index])
				d3.select(this).classed("dim", true);
			else
				// Display full name for highlighted visibleNodes
				d3.select(this).selectAll("text")
					.text(function(d) {
						return d.name;
					})
		});
	};

	// Set the graph's width and height
	this.updateHeightAndWidth = function () {
		this.w = $(window).width();
		this.h = $(window).height();
		svg.attr("width", this.w)
	    	.attr("height", this.h);
		
		vis.attr("width", this.w)
    	.attr("height", this.h);
		
	}

	this.setNodeCharge = function(charge) {
		if (isNaN(charge)) return;
		this.force.stop();
		this.nodecharge = charge;
		for (n in nodes) {
			nodes[n].applytoChildren(function(d) {
				d.charge = charge;
				d.defaultcharge = charge;
			});
		};
		
		this.update();

	}
	
	this.setLinkLength = function(length) {
		if (isNaN(length)) return;
		graph.linklength = length;
		for (l in links) {
			links[l].length = length;
		};
		
		this.update();

	}
	
	this.setChargeDistance = function(dist) {
		if (isNaN(dist)) return;
		this.force.force("charge").distanceMax(dist);
		
		this.update();

	}

	this.toggleFixedMode = function(setfixed) {
		this.fixedMode = setfixed;
		
		if (setfixed) {
			this.pause();
		}
		else {
		    this.resume();
		}
		this.tick();
	}

	this.toggleGravity = function(enabled) {
		if (enabled) {
			this.force.force("center", d3.forceCenter(this.w/2, this.h/2));		}
		else {
			this.force.force("center", null);
		}
		this.update();

	}
	
	this.setFriction = function(friction) {
		this.force.velocityDecay(friction);
	}

	this.pause = function() {
		this.active = false;
		this.force.stop();
	}
	this.resume = function() {
		this.active = true;
		this.force
    	.alphaTarget(1)
    	.restart();
	}
	
	this.updateHeightAndWidth();
	// Run it
	this.update();
}
