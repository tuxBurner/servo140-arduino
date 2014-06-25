/**
 * Created by tuxburner on 6/10/14.
 */


// stores all buttons in the html
var buttons = null;

// sequence for the part ids
var partIdSeq = 0;


// stores if a circle is bounded or not.
var boundedInfos = new Object();

//http://stackoverflow.com/a/18473154/3058835
function polarToCartesian(centerX, centerY, radius, angleInDegrees) {
    var angleInRadians = (angleInDegrees - 90) * Math.PI / 180.0;

    return {
        x: centerX + (radius * Math.cos(angleInRadians)),
        y: centerY + (radius * Math.sin(angleInRadians))
    };
}

function describeArc(x, y, radius, startAngle, endAngle) {
    var start = polarToCartesian(x, y, radius, endAngle);
    var end = polarToCartesian(x, y, radius, startAngle);
    var arcSweep = endAngle - startAngle <= 180 ? "0" : "1";
    return "M" + start.x + "," + start.y + " A" + radius + "," + radius + " 0 " + arcSweep + " 0 " + end.x + "," + end.y;
}

// ratio for calc is 4.62962963 * cm
// avaible partTypes
var partTypes = {
    curve90: {
        width: 112.96,
        height: 112.96,
        connections: [
            {
                x: 0,
                y: 33.33,
                acceptedRotation: 0
            },
            {
                x: 79.63,
                y: 112.96,
                acceptedRotation: 90
            }
        ],
        paths: [
            {
                path: describeArc(0, 112.96, 79.63, 0, 90),
                stroke: "black",
                strokeWidth: 66.66
            },
            {
                path: describeArc(0, 112.96, 79.63, 0, 90),
                stroke: "white",
                strokeWidth: 4
            }
        ]
    },
    curve45: {
        width: 79.63,
        height: 79.63,
        connections: [
            {
                x: 0,
                y: 33.33,
                acceptedRotation: 0
            },
            {
                x: 56,
                y: 57,
                acceptedRotation: 45
            }
        ],
        paths: [
            {
                path: describeArc(0, 112.96, 79.63, 0, 45),
                stroke: "black",
                strokeWidth: 66.66
            },
            {
                path: describeArc(0, 112.96, 79.63, 0, 45),
                stroke: "white",
                strokeWidth: 4
            }
        ]
    },
    curve452: {
        width: 159.26,
        height: 159.26,
        connections: [
            {
                x: 0,
                y: 66.66,
                acceptedRotation: 0
            },
            {
                x: 112,
                y: 114,
                acceptedRotation: 45
            }
        ],
        paths: [
            {
                path: describeArc(0, 225.92, 159.26, 0, 45),
                stroke: "black",
                strokeWidth: 66.66
            },
            {
                path: describeArc(0, 225.92, 159.26, 0, 45),
                stroke: "white",
                strokeWidth: 4
            }
        ]
    }
};


var createStraight = function (width, height, fillColor) {
    var halfHeight = height / 2;
    return {
        width: width,
        height: height,
        connections: [
            {x: 0,
                y: halfHeight,
                acceptedRotation: 0
            },
            {
                x: width,
                y: halfHeight,
                acceptedRotation: 0
            }
        ],
        paths: [
            {
                path: "M0,0 " + width + ",0 " + width + "," + height + " 0," + height + " Z",
                fill: fillColor,
                stroke: "white",
                strokeWidth: 1
            },
            {
                path: "M0," + halfHeight + " L " + width + "," + halfHeight,
                stroke: "white",
                strokeWidth: 4
            }
        ]
    }
}

partTypes['straight'] = createStraight(100, 66.66, 'black');
partTypes['dblStraight'] = createStraight(200, 66.66, 'black');
partTypes['connectStraight'] = createStraight(200, 66.66, 'gray');
partTypes['thirdStraight'] = createStraight(33.33, 66.66, 'black');
partTypes['fourthStraight'] = createStraight(25, 66.66, 'black');


// list of parts avaible
var parts = Array();

// the currently selected part
var currentSelectedPart = null;

var moveToLayer = function (layer) {
    if (currentSelectedPart == null) {
        return;
    }
    currentSelectedPart.setZIndex(layer);
}

// rotates the current selected part
var rotatePart = function (rotateRight) {
    if (currentSelectedPart == null) {
        return;
    }

    var currentRotation = calcRotation(rotateRight, currentSelectedPart.getRotation());
    currentSelectedPart.setRotation(currentRotation);

    rotateCircles(currentSelectedPart, rotateRight);
    layer.draw();
}

var rotateCircles = function (part, rotateRight) {
    for (i = 0; i < part.detectionCirles.length; i++) {
        part.detectionCirles[i].acceptedRotation = calcRotation(rotateRight, part.detectionCirles[i].acceptedRotation);
        colorCirclesForDebug(part.detectionCirles[i]);
    }
}

var deletePart = function (callback) {
    if (currentSelectedPart == null) {
        return;
    }

    unboundPart(currentSelectedPart);

    currentSelectedPart.destroyChildren();
    currentSelectedPart.destroy();

    for (i = 0; i < parts.length; i++) {
        if (parts[i].id == currentSelectedPart.id) {
            parts.splice(i, 1);
            break;
        }
    }

    if (callback != undefined) {
        callback(currentSelectedPart)
    }

    enableDisableTrackPartButton(false);

    currentSelectedPart = null;

    layer.draw();
}

var exportData = function (callback) {
    if (callback != undefined) {

        stage.setAbsolutePosition({x:0, y:0});
        var moveToX = 0;
        var moveToY = 0;
        var maxX = 0;
        var maxY = 0;
        for(var i=0;i <parts.length; i++) {
            var absPos = parts[i].getAbsolutePosition();
            if(absPos.x < moveToX) moveToX = absPos.x;
            if(absPos.y < moveToY) moveToY = absPos.y;

            if(absPos.x > maxX) maxX = absPos.x;
            if(absPos.y > maxY) maxY = absPos.y;
        }


        moveToX=moveToX*-1+100;
        moveToY=moveToY*-1+100;

        stage.setHeight(maxY+400);
        stage.setWidth(maxX+400);

        for(var i=0;i <parts.length; i++) {
            parts[i].move({x: moveToX, y: moveToY});
        }
        layer.setScale({x: 1, y:1});

        layer.draw();
        stage.draw();



        stage.toDataURL({
            callback: function (dataUrl) {
                var json = stage.toJSON();
                callback(json, dataUrl);
            }
        });

    }
}

var colorCirclesForDebug = function (circle) {
    var color = "";
    switch (circle.acceptedRotation) {
        case 0:
            color = 'red';
            break;
        case 45:
            color = 'orange';
            break;
        case 90:
            color = 'green';
            break;
        case 135:
            color = 'yellow';
            break;
        case 180:
            color = 'red';
            break;
        case 225:
            color = 'orange';
            break;
        case 270:
            color = 'green';
            break;
        case 315:
            color = 'yellow';
            break;
    }
    circle.fill(color);
    circle.setOpacity(1);
}


var calcRotation = function (rotateRight, currentRotation) {
    if (rotateRight == true) {
        currentRotation += 45;
        if (currentRotation == 360) {
            currentRotation = 0;
        }
    } else {
        if (currentRotation == 0) {
            currentRotation = 360;
        }
        currentRotation -= 45;
    }

    return currentRotation;
}

var calcRotationForPoints = function (part, newRotation, oldRotation) {
    var rotatetDegress = (newRotation > oldRotation) ? newRotation - oldRotation : oldRotation - newRotation;
    // which direction
    var direction = (newRotation > oldRotation);
    var steps = rotatetDegress / 45;
    for (i = 0; i < steps; i++) {
        rotateCircles(part, direction);
    }
}

var highlightPart = function (partGroup) {
    if (currentSelectedPart != null) {
        currentSelectedPart.find('.highlight').hide();
    }

    // deselect the current part
    if (currentSelectedPart != null && currentSelectedPart.getId() == partGroup.getId()) {
        currentSelectedPart = null;
        enableDisableTrackPartButton(false);
    } else {
        currentSelectedPart = partGroup;
        currentSelectedPart.find('.highlight').show();
        enableDisableTrackPartButton(true);
    }
    layer.draw();
}

var enableDisableTrackPartButton = function (enable) {
    if (buttons == null) {
        buttons = document.getElementsByClassName('selected_part_btn');
    }
    for (button in buttons) {
        buttons[button].disabled = !enable;
    }
}

var unboundPart = function (part) {
    // check if any part is bounded to this part and remove the bounding from it
    for (i = 0; i < part.detectionCirles.length; i++) {
        var boundedCircle = boundedInfos[part.detectionCirles[i].getId()];
        if (boundedCircle != null) {
            boundedCircle.show();
            boundedInfos[boundedCircle.getId()] = null;
            boundedInfos[part.detectionCirles[i].getId()] = null;
            part.detectionCirles[i].show();
            layer.draw();
        }
    }
}

// function for adding a new part to the tracke
var addPart = function (partName) {

    if (partTypes[partName] == null) {
        return;
    }

    var partType = partTypes[partName];

    var partId = partIdSeq++;

    var group = new Kinetic.Group({
        x: 0,
        y: 0,
        rotation: 0,
        draggable: true,
        id: 'part_' + partId,
        name: 'trackPart',
        trackPartType: partName
    });
    group.detectionCirles = new Array();


    var pathGroup = new Kinetic.Group({
        x: 0,
        y: 0
    });

    for (i = 0; i < partType.paths.length; i++) {
        var pathInfo = partType.paths[i];
        var kPath = new Kinetic.Path({
            x: 0,
            y: 0,
            data: pathInfo.path,
            fill: pathInfo.fill,
            stroke: pathInfo.stroke,
            strokeWidth: pathInfo.strokeWidth
        });
        pathGroup.add(kPath);
    }

    group.add(pathGroup);


    var pathInfo = partType.paths[0];
    var highlightrect = new Kinetic.Path({
        x: 0,
        y: 0,
        fill: 'green',
        stroke: 'green',
        strokeWidth: pathInfo.strokeWidth,
        data: pathInfo.path,
        name: 'highlight',
        opacity: 0.5
    });


    group.add(highlightrect);
    highlightPart(group);


    // add the connection points
    for (i = 0; i < partType.connections.length; i++) {
        var circle = new Kinetic.Circle({
            x: partType.connections[i].x,
            y: partType.connections[i].y,
            radius: 10,
            stroke: 'black',
            strokeWidth: 1,
            id: 'circle_' + partId + '_' + i
        });
        circle.acceptedRotation = partType.connections[i].acceptedRotation;
        colorCirclesForDebug(circle);
        group.add(circle);
        group.detectionCirles.push(circle);
    }

    layer.add(group);
    parts.push(group);

    group.setX(((stage.getWidth() / 2) - stage.getAbsolutePosition().x) / stage.getScale().y);
    group.setY(((stage.getHeight() / 2) - stage.getAbsolutePosition().y) / stage.getScale().y);
    layer.draw();

    group.on('dragstart', function () {
        unboundPart(this);
        if (currentSelectedPart != null && currentSelectedPart.getId() != this.getId()) {
            highlightPart(this);
        }
    });


    group.on('dragend', function () {
        if (parts.length == 1) {
            return;
        }
        for (h = 0; h < parts.length; h++) {
            var part = parts[h];
            if (part.getId() != this.getId()) {
                for (j = 0; j < part.detectionCirles.length; j++) {

                    // check if the part circle is already bound
                    if (boundedInfos[part.detectionCirles[j].getId()] != null) {
                        continue;
                    }

                    var partPos = part.detectionCirles[j].getAbsolutePosition();

                    // check if the draged part circles are in the sticky attach range
                    for (i = 0; i < this.detectionCirles.length; i++) {
                        var circlPos = this.detectionCirles[i].getAbsolutePosition();

                        var distanceX = circlPos.x - partPos.x;
                        var distanceY = circlPos.y - partPos.y;
                        var distance = Math.sqrt((distanceX * distanceX) + (distanceY * distanceY));
                        // lets attach the part to the other one
                        if (distance <= 15 * stage.getScale().y) {
                            this.offsetX(this.detectionCirles[i].getX());
                            this.offsetY(this.detectionCirles[i].getY());

                            // check if to rotate the part
                            /*var circleRotation = part.detectionCirles[j].acceptedRotation;
                             var circleRotation2 = (circleRotation2 <= 180) ? circleRotation + 180 :  circleRotation - 180;
                             if(this.getRotation() != circleRotation  && this.getRotation() != circleRotation2) {
                             calcRotationForPoints(this,circleRotation,this.getRotation());
                             this.setRotation(circleRotation);
                             } */


                            this.setX((partPos.x / layer.getScale().y) - (stage.getAbsolutePosition().x / layer.getScale().y));
                            this.setY((partPos.y / layer.getScale().y) - (stage.getAbsolutePosition().y / layer.getScale().y));

                            // mark the track part ends bounded
                            part.detectionCirles[j].hide();
                            this.detectionCirles[i].hide();
                            boundedInfos[this.detectionCirles[i].getId()] = part.detectionCirles[j];
                            boundedInfos[part.detectionCirles[j].getId()] = this.detectionCirles[i];
                        }
                    }
                }
            }
        }
        layer.draw();
    });

    group.on('click', function () {
        highlightPart(this);
    });
}


var stage = new Kinetic.Stage({
    container: 'trackEditorContainer',
    width: window.innerWidth - 350,
    height: window.innerHeight - 200,
    draggable: true
});


var layer = new Kinetic.Layer();
//layer.setScale({x: 0.52, y: 0.52});
// add the layer to the stage
stage.add(layer);


var zoom = function (e) {
    e.preventDefault();
    var zoomAmount = e.wheelDeltaY * 0.001;
    var scale = layer.getScale().y + zoomAmount;
    layer.setScale({x: scale, y: scale});
    layer.draw();
}
document.addEventListener("mousewheel", zoom, false)

// TODO REMOVE from here :)
$(function () {
    $.each(avaibleParts, function (i, obj) {
        if (obj > 0) {
            $('#trackEditorPartsBtns').append('<button class="btn" onclick="addPart(\'' + i + '\');">' + i + '</button>');
        }
    });
})
