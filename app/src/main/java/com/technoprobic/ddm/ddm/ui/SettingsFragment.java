package com.technoprobic.ddm.ddm.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;

import com.technoprobic.ddm.ddm.R;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.rx.Web3jRx;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {

    final String testEthereumPrivateKey = "0xC89ADA337DCDD9D9D092D582104064554DDC3A835B0D164B82E304F0DFC5F0FC"; // from DDM repo in RegistrationForm.js
    final String testDdmContractAddressRopsten = "0x38A4efD7A5042B694fD138376D5279AEC8083e37"; //from DDM repo in storehash.js
    final String testIotaSeed = "LXQHWNY9CQOHPNMKFJFIJHGEPAENAOVFRDIBF99PPHDTWJDCGHLYETXT9NPUVSNKT9XDTDYNJKJCPQMZC"; // from

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings);

        Preference defaultsPreference = findPreference(getString(R.string.settings_load_test_addresses_key));
        defaultsPreference.setOnPreferenceChangeListener(this);

        Preference ddmPreference = findPreference(getString(R.string.settings_ddm_contract_address_key));
        ddmPreference.setOnPreferenceChangeListener(this);

        Preference ethereumPreference = findPreference(getString(R.string.settings_ethereum_private_key_key));
        ethereumPreference.setOnPreferenceChangeListener(this);

        Preference iotaPreference = findPreference(getString(R.string.settings_iota_seed_key));
        iotaPreference.setOnPreferenceChangeListener(this);

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        setPreferenceSummary(findPreference(getString(R.string.settings_ddm_contract_address_key)), sharedPreferences.getString(getString(R.string.settings_ddm_contract_address_key), "") );
        setPreferenceSummary(findPreference(getString(R.string.settings_ethereum_private_key_key)), sharedPreferences.getString(getString(R.string.settings_ethereum_private_key_key), "") );
        setPreferenceSummary(findPreference(getString(R.string.settings_iota_seed_key)), sharedPreferences.getString(getString(R.string.settings_iota_seed_key), "") );

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Preference preference = findPreference(key);
        if (preference != null) {
            if (preference instanceof EditTextPreference) {
                String preferenceValue = sharedPreferences.getString(preference.getKey(), "");
                setPreferenceSummary(preference, preferenceValue);
            }
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        if (preference.getKey().equals(getString(R.string.settings_load_test_addresses_key) ) ) { // load test addresses

            SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getString(R.string.settings_ddm_contract_address_key), testDdmContractAddressRopsten);
            editor.putString(getString(R.string.settings_ethereum_private_key_key), testEthereumPrivateKey);
            editor.putString(getString(R.string.settings_iota_seed_key), testIotaSeed);
            editor.commit();

            return false;

        } else if (preference.getKey().equals(getString(R.string.settings_ddm_contract_address_key) ) ) {
            String newContractAddress = (String) newValue;
            // todo validate ethereum address, consider QR code reader, fork DDM and add QR code with address
            return true;

        } else if (preference.getKey().equals(getString(R.string.settings_ethereum_private_key_key) ) ) {
            String newPrivateKey = (String) newValue;
            // todo validate ethereum private key, consider wallet file or QR code reader
            return true;

        } else if (preference.getKey().equals(getString(R.string.settings_iota_seed_key) ) ) {
            String newIotaSeed = (String) newValue;
            // todo validate iota seed, consider QR code reader
            return true;
        }

        return true;
    }

    private void setPreferenceSummary(Preference preference, String preferenceSummaryValue) {
        if (preference instanceof EditTextPreference) {
           preference.setSummary(preferenceSummaryValue);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
