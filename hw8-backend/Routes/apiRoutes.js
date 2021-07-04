const axios = require('axios');
const request = require('request');
const ngeohash = require('ngeohash');
const SpotifyWebApi = require('spotify-web-api-node');
module.exports = app => {
    function get_segmentID(category) {
        if (category.toLowerCase() === 'all'){
            return 'all';
        }
        else if(category.toLowerCase() === 'music'){
            return 'KZFzniwnSyZfZ7v7nJ';
        }
        else if(category.toLowerCase() === 'sports'){
            return 'KZFzniwnSyZfZ7v7nE';
        }
        else if(category.toLowerCase() === 'arts & theatre'.toLowerCase()) {
            return 'KZFzniwnSyZfZ7v7na';
        }
        else if(category.toLowerCase() === 'film'){
            return 'KZFzniwnSyZfZ7v7nn';
        }
        else if(category.toLowerCase() === 'miscellaneous'){
            return 'KZFzniwnSyZfZ7v7n1';
        }
        else{
            return 'not support';
        }
    }

    app.get("/event", async (req, res) => {
        var keyword = req.query.keyword;
        keyword.replace(' ', '+');
        var category = req.query.category;
        var segmentID = get_segmentID(category)
        var distance = req.query.distance;
        var unit = req.query.distance_unit;
        if(unit.toLowerCase() === 'miles'){
            unit = 'miles';
        }else{
            unit = 'km';
        }
        var here = req.query.here;
        if(here.toLowerCase() === 'true'){
            console.log('here radio selected');
            var loc = req.query.loc;
            console.log('loc: ' + loc);
            var loc_result = loc.split(",").map(function(item) {
                return item.trim();
            });
            console.log(loc_result);
            var lat = loc_result[0];
            var lng = loc_result[1];
            console.log("lat: "+lat+" lng: " + lng);
            const geohash = ngeohash.encode(lat, lng, 7);
            console.log("geohash: "+ geohash);
            var ticket_params = {
                'apikey': 'JRcB0khf4v8xp9g8fMoYvyywRYphbGmS',
                'keyword': keyword,
                'radius': distance,
                'unit': unit,
                'geoPoint': geohash
            };
            if( segmentID !== 'all' && segmentID !== 'not support'){
                ticket_params['segmentId'] = segmentID;
            }
            console.log(ticket_params);
            var ticket_url = 'https://app.ticketmaster.com/discovery/v2/events.json?'
            request({url:ticket_url, qs:ticket_params}, function(err, response, body) {
                if(err) { console.log(err); return; }
                console.log("Get response: " + response.statusCode);
                var obj = JSON.parse(body);
                res.json(obj);
            });
        }else{
            var address = req.query.address;
            const googleMaps_params = new URLSearchParams({
                'key': 'AIzaSyD4jP8bWnT6yKN0P4EBmPp2KuYAI35vV9E',
                'address': address
            }).toString();
            const googleMaps_url ='https://maps.googleapis.com/maps/api/geocode/json?'+ googleMaps_params;
            axios.get(googleMaps_url)
                .then((response) => {
                    var geometry = response.data['results'][0]['geometry'];
                    lat = geometry['location']['lat'];
                    lng = geometry['location']['lng'];
                    console.log("google lat: "+ lat + " lng: "+ lng);
                    console.log("lat: "+lat+" lng: " + lng);
                    const geohash = ngeohash.encode(lat, lng, 7);
                    console.log("geohash: "+ geohash);
                    var ticket_params = {
                        'apikey': 'JRcB0khf4v8xp9g8fMoYvyywRYphbGmS',
                        'keyword': keyword,
                        'radius': distance,
                        'unit': unit,
                        'geoPoint': geohash
                    };
                    if( segmentID !== 'all' && segmentID !== 'not support'){
                        ticket_params['segmentId'] = segmentID;
                    }
                    console.log(ticket_params);
                    var ticket_url = 'https://app.ticketmaster.com/discovery/v2/events.json?'
                    request({url:ticket_url, qs:ticket_params}, function(err, response, body) {
                        if(err) { console.log(err); return; }
                        console.log("Get response: " + response.statusCode);
                        var obj = JSON.parse(body);
                        res.json(obj);
                    });
                });
        }
    });

    app.get("/eventdetails", (req, res) => {
        let id = req.query.id;
        let url = 'https://app.ticketmaster.com/discovery/v2/events/' + id + '?';
        let params = {
            'apikey': 'JRcB0khf4v8xp9g8fMoYvyywRYphbGmS'
        }
        request({url:url, qs:params}, function(err, response, body) {
            if(err) { console.log(err); return; }
            console.log("Details Get response: " + response.statusCode);
            var obj = JSON.parse(body);
            res.json(obj);
        });
    });

    app.get("/venuedetails", (req, res) => {
        let id = req.query.id;
        let url = 'https://app.ticketmaster.com/discovery/v2/venues/' + id + '.json?';
        let params = {
            'apikey': 'JRcB0khf4v8xp9g8fMoYvyywRYphbGmS'
        }
        request({url:url, qs:params}, function(err, response, body) {
            if(err) { console.log(err); return; }
            console.log("Venue Details Get response: " + response.statusCode);
            var obj = JSON.parse(body);
            res.json(obj);
        });
    });

    app.get("/autocomplete", (req, res) => {
        let keyword = req.query.keyword;
        let url = "https://app.ticketmaster.com/discovery/v2/suggest?";
        let params = {
            'apikey': 'JRcB0khf4v8xp9g8fMoYvyywRYphbGmS',
            'keyword': keyword
        }
        request({url:url, qs:params}, function(err, response, body) {
            if(err) { console.log(err); return; }
            let data = JSON.parse(body);
            let options = [];
            if(data['_embedded'] !== undefined){
                if(data['_embedded']['attractions'] !== undefined){
                    let attractions = data['_embedded']['attractions'];
                    for(let i = 0; i < attractions.length; i++){
                        if(attractions[i]['name'] !== undefined){
                            options.push(attractions[i]['name']);
                        }
                    }
                }
            }
            res.json(options);
        });
    });

    app.get('/spotify', (req, res)=>{
        let keyword = req.query.keyword;
        const clientId = '0edbbe09dce74d6c83258e967233341f',
            clientSecret = '39c5ef0c92c84239ba4e7a34e900bf09';
        var spotifyApi = new SpotifyWebApi({
            clientId: clientId,
            clientSecret: clientSecret
        });
        spotifyApi.searchArtists(keyword)
            .then(function(data) {
                console.log('Search artists by keyword', data.body);
                return res.json(data.body);
            }, function(err) {

                if(err.statusCode == 401){
                    spotifyApi.clientCredentialsGrant().then(
                        async function(data) {
                            console.log('The access token expires in ' + data.body['expires_in']);
                            console.log('The access token is ' + data.body['access_token']);

                            // Save the access token so that it's used in future calls
                            await spotifyApi.setAccessToken(data.body['access_token']);
                            await spotifyApi.searchArtists(keyword)
                                .then(function(data) {
                                    console.log('Search artists by keyword', data.body);
                                    return res.json(data.body);
                                }, function(err) {
                                    console.error(err);
                                });
                        },
                        function(err) {
                            console.log('Something went wrong when retrieving an access token', err);
                        }
                    );
                }else{
                    console.error('Error', err);
                }
            });

    });
};
