package com.example.ipcclient

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.Process
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.ipcclient.databinding.FragmentAidlBinding
import com.example.ipcserver.ServerAIDL

class AidlFragment : Fragment(), ServiceConnection, View.OnClickListener {

    private var _binding: FragmentAidlBinding? = null
    private val binding get() = _binding!!
    var iRemoteService: ServerAIDL? = null
    private var connected = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAidlBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.btnConnect.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        if (connected) {
            disconnectFromRemoteService()
            binding.txtServerPid.text = ""
            binding.txtServerConnectionCount.text = ""
            binding.btnConnect.text = getString(R.string.connect)
            binding.linearLayoutClientInfo.visibility = View.INVISIBLE
            connected = false
        } else {
            connectToRemoteService()
        }
    }

    private fun connectToRemoteService() {
        val intent = Intent("aidlexample")
        val pack = ServerAIDL::class.java.`package`
        pack?.let {
            intent.setPackage(pack.name)
            val success = activity?.applicationContext?.bindService(
                intent, this, Context.BIND_AUTO_CREATE
            ) ?: false
            if (success) {
                connected = true
                binding.linearLayoutClientInfo.visibility = View.VISIBLE
                binding.btnConnect.text = getString(R.string.disconnect)
            } else {
                Toast.makeText(context, "Failed to connect to the service", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun disconnectFromRemoteService() {
        if (connected) {
            activity?.applicationContext?.unbindService(this)
            connected = false
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        iRemoteService = ServerAIDL.Stub.asInterface(service)
        binding.txtServerPid.text = iRemoteService?.pid.toString()
        binding.txtServerConnectionCount.text = iRemoteService?.connectionCount.toString()
        iRemoteService?.setDisplayedValue(
            context?.packageName,
            Process.myPid(),
            binding.edtClientData.text.toString()
        )
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        Toast.makeText(context, "IPC server has disconnected unexpectedly", Toast.LENGTH_LONG).show()
        iRemoteService = null
        connected = false
    }
}
