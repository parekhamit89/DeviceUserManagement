package com.profile.deviceusermanagement

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.profile.deviceusermanagement.databinding.FragmentFirstBinding
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class UserDataListFragment : Fragment(), UpdateListener {
    private lateinit var deviceUserManager: DeviceUserManager
    private var _binding: FragmentFirstBinding? = null
    private lateinit var deviceUserList: ArrayList<UserData>
    private lateinit var adapater: UserRecyclerAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        val toolbar = container!!.findViewById<Toolbar>(R.id.toolbar)

        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)

        (activity as? AppCompatActivity)?.supportActionBar?.show()
        adapater = UserRecyclerAdapter()
        deviceUserManager = activity?.let { DeviceUserManager(it.baseContext, this) }!!

        deviceUserManager.readDeviceUser();

        return binding.root

    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sync -> {
//                deviceUserManager.writeModifiedFile()
//                TODO:startLoader()
                lifecycleScope.launch { deviceUserManager.updateUsers() }

                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var recyclerView = view.findViewById<RecyclerView>(R.id.usersRCView)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapater
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun deviceUserUpdate(updatedDeviceUserList: ArrayList<UserData>) {
        //TODO: dismiss loader
        deviceUserList = updatedDeviceUserList
        adapater.submitList(deviceUserList)
        Log.e("TODO", "deviceUserUpdate: ${deviceUserList.size}", )
    }
}