/**
 * 
 */

//null及"" 返回 0.00
function field_formatter_money(value, row, index){
	//console.log(typeof accounting);
	if(typeof accounting == "undefined"){
		console.log("account.js is not loaded or it's include after data_field.js");
		return value;
	}
	return accounting.formatMoney(value);
}

// null及"" 返回 "";
function field_formatter_money_strict(value, row, index){
	//console.log(typeof accounting);
	if(typeof accounting == "undefined"){
		console.log("account.js is not loaded or it's include after data_field.js");
		return value;
	}
	if(!value){
		return "";
	}
	return accounting.formatMoney(value);
}

function field_styler_highlight(value, row, index){
	//return {class:'highlighted',style:'color:red'};
	return {class:'highlighted'};
}

function field_formatter_simple_date(value, row, index){
	if(value != null){
		var s = value.substring(0,10);
		return s;
	}
}

function field_formatter_reward_points(value, row, index){
	if(value==null){
		return '';
	}
	if (value > 0){
        return '<span style="color:#36c6d3">+' + value + '</span>';
    }else if(value < 0){
    	return '<span style="color:#E43A45">' + value + '</font>';
    }else{
    	return '<font style="color:#000">' + value + '</font>';
    }
}
