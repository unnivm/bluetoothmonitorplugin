
import 'cordova.js';
class Bluetooth {
    
    constructor() {
        this.initialize();
    }

    initialize() {
        document.addEventListener('deviceready', this.onDeviceReady.bind(this), false);
    }

    onDeviceReady() {
        this.receivedEvent('deviceready');
    }

    receivedEvent(id) {
        let parentElement = document.getElementById(id);
        let listeningElement = parentElement.querySelector('.listening');
        let receivedElement = parentElement.querySelector('.received');
        listeningElement.setAttribute('style', 'display:none;');
        receivedElement.setAttribute('style', 'display:block;');
        console.log('Received Event: ' + id);
    }

    getBluetoothDeviceList() {
		alert(" get bluetooth devices..");
		
		cordova.exec(function(data) {
			console.log(data);
			alert(data);
		},
             function(error) {
				 console.log(error);
				 alert(" any error ->  " + error);
				 
			 },
             "BluetoothMonitor",
             "getBluetoothDeviceList"
             );
    }
}


// initialize Bluetooth
let bt = new Bluetooth();