var _ = require('underscore')._;
var PrecisionRecall = require("./PrecisionRecall");
var fs = require('fs');

var filename = process.argv[2]
var data = JSON.parse(fs.readFileSync(filename));

var stats_single = new PrecisionRecall();
var stats_majority = new PrecisionRecall();

_.each(data, function(value, key, list){ 
	
	if (!(_.isArray(value['output'])))
		value['output'] = JSON.parse(value['output'])
	
	if (!(_.isArray(value['tags_single'])))
		value['tags_single'] = JSON.parse(value['tags_single'])

	if (!(_.isArray(value['tags_majority'])))
		value['tags_majority'] = JSON.parse(value['tags_majority'])


	value['output'] = _.map(value['output'], function(json){ return _.isString(json)? json: JSON.stringify(json) });
	value['tags_single'] = _.map(value['tags_single'], function(json){ return _.isString(json)? json: JSON.stringify(json) });
	value['tags_majority'] = _.map(value['tags_majority'], function(json){ return _.isString(json)? json: JSON.stringify(json) });

	stats_single.addCasesHash(value['output'], value['tags_single'])
	stats_single.addCasesLabels(value['output'], value['tags_single'])
	
	stats_majority.addCasesHash(value['output'], value['tags_majority'])
	stats_majority.addCasesLabels(value['output'], value['tags_majority'])

}, this)

console.log("TAGS SINGLE")
console.log(stats_single.retrieveStats())
console.log(stats_single.retrieveLabels())

console.log("TAGS MAJORITY")
console.log(stats_majority.retrieveStats())
console.log(stats_majority.retrieveLabels())


