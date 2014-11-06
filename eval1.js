var _ = require('underscore')._;
var PrecisionRecall = require("./PrecisionRecall");
var fs = require('fs');

var filename = process.argv[2]
var data = JSON.parse(fs.readFileSync(filename));

var stats = new PrecisionRecall();

_.each(data, function(value, key, list){ 
	
	if (!(_.isArray(value['output'])))
		value['output'] = JSON.parse(value['output'])
	
	if (!(_.isArray(value['tags'])))
		value['tags'] = JSON.parse(value['tags'])


	value['output'] = _.map(value['output'], function(json){ return _.isString(json)? json: JSON.stringify(json) });
	value['tags'] = _.map(value['tags'], function(json){ return _.isString(json)? json: JSON.stringify(json) });

	stats.addCasesHash(value['output'], value['tags'])
	stats.addCasesLabels(value['output'], value['tags'])
	

}, this)

var single = stats.retrieveStats()
console.log("Precision "+single['Precision'])
console.log("Recall "+single['Recall'])
console.log("F1 "+single['F1'])

