package com.mukundafoods.chimneylauncherproduct.ui.profilefragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mukundafoods.chimneylauncherproduct.databinding.ProfileFragmentBinding
import com.mukundafoods.chimneylauncherproduct.ui.sharedpreference.Data

class ProfileFragment : Fragment() {


    private var _binding: ProfileFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = ProfileFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUserName()
        initEmail()
        initContact()
    }

    private fun initContact() {
        binding.contact.text = Data.getContact()
        binding.contactLayout.setOnClickListener {
            if (binding.editContactLayout.visibility == View.VISIBLE) {
                binding.editContactLayout.visibility = View.GONE
                binding.contactArrow.rotation = (90).toFloat()
            } else {
                binding.editContact.hint =
                    if (Data.getContact().isNullOrEmpty()) "Enter Contact" else Data.getContact()
                binding.editContactLayout.visibility = View.VISIBLE
                binding.contactArrow.rotation = (-90).toFloat()
            }
        }

        binding.saveContact.setOnClickListener {
            val contact = binding.editContact.text.toString()
            if (contact.isNotEmpty()) {
                binding.contactArrow.rotation = (90).toFloat()
                binding.editContactLayout.visibility = View.GONE
                Data.setContact(contact)
                binding.contact.text = contact
            }
        }
    }

    private fun initEmail() {
        binding.email.text = Data.getEmail()
        binding.emailLayout.setOnClickListener {
            if (binding.editEmailLayout.visibility == View.VISIBLE) {
                binding.editEmailLayout.visibility = View.GONE
                binding.emailArrow.rotation = (90).toFloat()
            } else {
                binding.editEmail.hint =
                    if (Data.getEmail().isNullOrEmpty()) "Enter Email" else Data.getEmail()
                binding.editEmailLayout.visibility = View.VISIBLE
                binding.emailArrow.rotation = (-90).toFloat()
            }
        }

        binding.saveEmail.setOnClickListener {
            val email = binding.editEmail.text.toString()
            if (email.isNotEmpty()) {
                binding.emailArrow.rotation = (90).toFloat()
                binding.editEmailLayout.visibility = View.GONE
                Data.setEmail(email)
                binding.email.text = email
            }
        }
    }

    private fun initUserName() {
        binding.userName.text = Data.getUserName()
        binding.userNameLayout.setOnClickListener {
            if (binding.editUserNameLayout.visibility == View.VISIBLE) {
                binding.editUserNameLayout.visibility = View.GONE
                binding.userNameArrow.rotation = (90).toFloat()
            } else {
                binding.editUserName.hint =
                    if (Data.getUserName().isNullOrEmpty()) "Enter Name" else Data.getUserName()
                binding.editUserNameLayout.visibility = View.VISIBLE
                binding.userNameArrow.rotation = (-90).toFloat()
            }
        }

        binding.saveUserName.setOnClickListener {
            val updatedName = binding.editUserName.text.toString()
            if (updatedName.isNotEmpty()) {
                binding.userNameArrow.rotation = (90).toFloat()
                binding.editUserNameLayout.visibility = View.GONE
                Data.setUserName(updatedName)
                binding.userName.text = updatedName
            }
        }
    }
}