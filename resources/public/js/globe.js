var countryFeature, pointFeature;

var projection = d3.geo.azimuthal()
    .scale(380)
    .origin([-71.03, 0])
    .mode("orthographic")
    .translate([640, 400]);

var circle = d3.geo.greatCircle()
    .origin(projection.origin());

var path = d3.geo.path()
    .projection(projection)
    .pointRadius(2);

var svg = d3.select("#globe").append("svg:svg")
    .attr("width", 1280)
    .attr("height", 800)
    .on("mousedown", mousedown);

var countries = svg.append("svg:g")
    .attr("id", "countries");

var points = svg.append("svg:g")
    .attr("id", "points");

d3.json("/js/v/world-countries.json", function(collection) {
  countryFeature = countries.selectAll("path")
    .data(collection.features)
    .enter().append("svg:path")
    .attr("id", function(d) { return d.id; })
    .attr("class", "country")
    .attr("d", clip);

  countryFeature.append("svg:title")
    .text(function(d) { return d.properties.name; });

  rotate();
});

d3.json("/points", function(collection) {
  console.log(collection.features);
  pointFeature = points.selectAll("circle")
    .data(collection.features)
    .enter().append("svg:path")
      .attr("class", "point")
      .attr("d", function(d) { return clip(d); });
});

d3.select(window)
    .on("mousemove", mousemove)
    .on("mouseup", mouseup);

var m0,
    o0;

function mousedown() {
  m0 = [d3.event.pageX, d3.event.pageY];
  o0 = projection.origin();
  d3.event.preventDefault();
}

function mousemove() {
  if (m0) {
    var m1 = [d3.event.pageX, d3.event.pageY],
        o1 = [o0[0] + (m0[0] - m1[0]) / 8, o0[1] + (m1[1] - m0[1]) / 8];
    projection.origin(o1);
    circle.origin(o1)
    refresh();
  }
}

function mouseup() {
  if (m0) {
    mousemove();
    m0 = null;
  }
}

function refresh() {
  countryFeature.attr("d", clip);
  if (pointFeature) { pointFeature.attr("d", clip); }
}

function clip(d) {
  return path(circle.clip(d));
}

function rotate() {
  d3.timer(function() {
    var origin = projection.origin();
    origin = [origin[0] - 0.18, origin[1]];
    projection.origin(origin);
    circle.origin(origin);
    refresh();
    return false;
  });
}
