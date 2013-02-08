var map = undefined;
var marker = undefined;
var position = [43, -89];

function initialize() {

	var latlng = new google.maps.LatLng(position[0], position[1]);
	var myOptions = {
		zoom: 8,
		center: latlng,
		disableDefaultUI: true,
		mapTypeId: google.maps.MapTypeId.ROADMAP
	};
	map = new google.maps.Map(document.getElementById("map"), myOptions);

	marker = new google.maps.Marker({
		position: latlng,
		map: map,
		title: "Your current location!"
	});

	google.maps.event.addListener(map, 'click', function(me) {
		var result = [me.latLng.lat(), me.latLng.lng()];
		transition(result);
	});

	

}

var numDeltas = 100;
var delay = 10; //milliseconds
var i = 0;
var deltaLat;
var deltaLng;
function transition(result){
	i = 0;
	deltaLat = (result[0] - position[0])/numDeltas;
	deltaLng = (result[1] - position[1])/numDeltas;
	moveMarker();
}

function moveMarker(){
	position[0] += deltaLat;
	position[1] += deltaLng;
	var latlng = new google.maps.LatLng(position[0], position[1]);
	marker.setPosition(latlng);
	if(i!=numDeltas){
		i++;
		setTimeout(moveMarker, delay);
	}
}

function formatSeconds(seconds) {
	hours = parseInt( seconds / 3600 ) % 24;
	minutes = parseInt( seconds / 60 ) % 60;
	seconds = parseInt(seconds % 60, 10);

	return (hours < 10 ? "0" + hours : hours) + ":" + (minutes < 10 ? "0" + minutes : minutes) + ":" + (seconds  < 10 ? "0" + seconds : seconds);
}

function build_slider(){

	var slider = $("#slider");
	var startingSeconds = 0;
	$( "#time" ).text( formatSeconds( startingSeconds ) );

	slider.slider({
		min: 0,
		max: 86399,
		step: 1,

		slide: function( event, ui ) {
			$( "#time" ).text( formatSeconds( ui.value ) );
		}

	});


}



$(document).ready(function() {
	initialize();
	build_slider();
});

