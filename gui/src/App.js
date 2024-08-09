import React, { useState } from 'react';
const { ipcRenderer } = window.require('electron');

const App = () => {
    // Imposta i valori di default
    const [organization, setOrganization] = useState('fatate');
    const [broker, setBroker] = useState('http://localhost:8080');
    const [isLocalhost, setIsLocalhost] = useState(true);

    const start = () => {
        console.log('Organization:', organization);
        console.log('Broker Address:', broker);
        console.log('Is Localhost:', isLocalhost);
        ipcRenderer.send("Start", [organization, broker, isLocalhost]);
    };

    const isStartDisabled = !organization || !broker;

    return (
        <div style={{ margin: '20px' }}>
            <h1>Worker GUI</h1>
            <label htmlFor="organization">Organization</label>
            <input
                type="text"
                id="organization"
                name="organization"
                value={organization}
                onChange={(e) => setOrganization(e.target.value)}
                style={{ width: '100%', padding: '8px', marginTop: '5px', boxSizing: 'border-box' }}
            />
            <label htmlFor="broker">Broker Address</label>
            <input
                type="text"
                id="broker"
                name="broker"
                value={broker}
                onChange={(e) => setBroker(e.target.value)}
                style={{ width: '100%', padding: '8px', marginTop: '5px', boxSizing: 'border-box' }}
            />

            <label htmlFor="isLocalhost" style={{ display: 'block', marginTop: '10px' }}>
                <input
                    type="checkbox"
                    id="isLocalhost"
                    name="isLocalhost"
                    checked={isLocalhost}
                    onChange={(e) => setIsLocalhost(e.target.checked)}
                    style={{ marginRight: '10px' }}
                />
                Is Localhost
            </label>

            <button 
                onClick={start} 
                style={{ 
                    marginTop: '20px', 
                    padding: '10px 15px', 
                    backgroundColor: isStartDisabled ? '#CCCCCC' : '#007BFF', 
                    color: 'white', 
                    border: 'none', 
                    cursor: isStartDisabled ? 'not-allowed' : 'pointer' 
                }} 
                disabled={isStartDisabled}
            >
                Start
            </button>
        </div>
    );
};

export default App;
