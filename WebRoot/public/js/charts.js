/**
 * 
 */
 var initChart = function(div, data, theme) {
	var chart_wrapper = $("#"+ div);
	var theme = theme || "red";
	
	var data_length = data.length; //fix the x axis last label overflow hidden bug. 
	//console.log(data.length);
	if(theme == "red"){
		//红色
	    var plot_statistics = $.plot(
	    		chart_wrapper, [{
	            data: data,
	            lines: {
	                fill: 0.6,
	                lineWidth: 0
	            },
	            color: ['#f89f9f']
	        }, {
	            data: data,
	            points: {
	                show: true,
	                fill: true,
	                radius: 5,
	                fillColor: "#f89f9f",
	                lineWidth: 3
	            },
	            color: '#fff',
	            shadowSize: 0
	        }], {
	
	            xaxis: {
	                tickLength: 0,
	                tickDecimals: 0,
	                mode: "categories",
	                min: -1,
	                max:data_length,
	                font: {
	                    lineHeight: 15,
	                    size:11,
	                    family: "Tahoma",
	                    style: "normal",
	                    color: "#6F7B8A"
	                }
	            },
	            yaxis: {
	                ticks: 3,
	                tickDecimals: 0,
	                tickColor: "#f0f0f0",
	                font: {
	                    lineHeight: 15,
	                    style: "normal",
	                    //variant: "small-caps",
	                    color: "#6F7B8A"
	                }
	            },
	            grid: {
	                backgroundColor: {
	                    colors: ["#fff", "#fff"]
	                },
	                borderWidth: 1,
	                borderColor: "#f0f0f0",
	                margin: 0,
	                minBorderMargin: 0,
	                labelMargin: 20,
	                hoverable: true,
	                clickable: true,
	                mouseActiveRadius: 6
	            },
	            legend: {
	                show: false
	            }
	        }
	    );  
	}else if(theme == "blue"){
		//蓝色
		var plot_statistics = $.plot(
				chart_wrapper, [{
	                data: data,
	                lines: {
	                    fill: 0.6,
	                    lineWidth: 0
	                },
	                color: ['#BAD9F5']
	            }, {
	                data: data,
	                points: {
	                    show: true,
	                    fill: true,
	                    radius: 5,
	                    fillColor: "#BAD9F5",
	                    lineWidth: 3
	                },
	                color: '#fff',
	                shadowSize: 0
	            }], {

	                xaxis: {
	                    tickLength: 0,
	                    tickDecimals: 0,
	                    mode: "categories",
		                min: -1,
		                max:data_length,
	                    font: {
	                        lineHeight: 14,
	                        style: "normal",
	                        family: "Tahoma",
	                        //variant: "small-caps",
	                        color: "#6F7B8A"
	                    }
	                },
	                yaxis: {
	                    ticks: 3,
	                    tickDecimals: 0,
	                    tickColor: "#f0f0f0",
	                    font: {
	                        lineHeight: 14,
	                        style: "normal",
	                        variant: "small-caps",
	                        color: "#6F7B8A"
	                    }
	                },
	                grid: {
	                    backgroundColor: {
	                        colors: ["#fff", "#fff"]
	                    },
	                    borderWidth: 1,
	                    borderColor: "#f0f0f0",
	                    margin: 0,
	                    minBorderMargin: 0,
	                    labelMargin: 20,
	                    hoverable: true,
	                    clickable: true,
	                    mouseActiveRadius: 6
	                },
	                legend: {
	                    show: false
	                }
	            }
	        );
	}

    var previousPoint = null;

    chart_wrapper.bind("plothover", function(event, pos, item) {
        $("#x").text(pos.x.toFixed(2));
        $("#y").text(pos.y.toFixed(2));
        if (item) {
            if (previousPoint != item.dataIndex) {
                previousPoint = item.dataIndex;

                $("#tooltip").remove();
                var x = item.datapoint[0].toFixed(2),
                    y = item.datapoint[1].toFixed(2);

                showTooltip(item.pageX, item.pageY, item.datapoint[0], item.datapoint[1]);
            }
        } else {
            $("#tooltip").remove();
            previousPoint = null;
        }
    });

};

function showTooltip(x, y, labelX, labelY) {
    $('<div id="tooltip" class="chart-tooltip">' + labelY + '</div>').css({
        position: 'absolute',
        display: 'none',
        top: y - 25,
        left: x + 10,
        border: '0px solid #ccc',
        padding: '2px 6px',
        'background-color': '#fff'
    }).appendTo("body").fadeIn(200);
}
 
 