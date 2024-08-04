import React, { useState } from 'react';

const App = () => {
    const [organization, setOrganization] = useState('');
    const [broker, setBroker] = useState('');

    const saveSettings = () => {
        console.log('Organization:', organization);
        console.log('Broker Address:', broker);
    };

    return (
        <div style={{ margin: '20px' }}>
            <h1>Configurazione</h1>
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
            <button onClick={saveSettings} style={{ marginTop: '20px', padding: '10px 15px', backgroundColor: '#007BFF', color: 'white', border: 'none', cursor: 'pointer' }}>
                Save
            </button>
        </div>
    );
};

export default App;
