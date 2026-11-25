package com.example.lta_macho.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lta_macho.BaseActivity
import com.example.lta_macho.R
import com.example.lta_macho.auth.AuthActivity
import com.example.lta_macho.databinding.FragmentHomeBinding
import com.example.lta_macho.ui.help.WebViewActivity
import com.example.lta_macho.ui.notes.NotesActivity
import com.example.lta_macho.ui.orders.OrderRepository
import com.example.lta_macho.ui.orders.ThirdActivity
import com.example.lta_macho.ui.profile.DeveloperProfileActivity
import com.example.lta_macho.ui.services.ServiceAdapter
import com.example.lta_macho.ui.services.ServiceItem
import com.example.lta_macho.ui.tasks.TasksActivity
import com.google.android.material.button.MaterialButton
import java.text.NumberFormat
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var orderRepository: OrderRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as? BaseActivity)?.setToolbarTitle("WASHTRACK BERANDA")
        orderRepository = OrderRepository(requireContext())

        setupGreeting()
        loadDashboardStatistics()
        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        loadDashboardStatistics()
    }

    private fun setupGreeting() {
        val sharedPref = requireActivity().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val userPref = requireActivity().getSharedPreferences("UserPref", Context.MODE_PRIVATE)

        val username = sharedPref.getString("username", null)
            ?: userPref.getString("username", null)

        if (!username.isNullOrEmpty()) {
            binding.tvGreeting.text = "Halo, $username!"
        } else {
            binding.tvGreeting.text = "Halo, Owner WashTrack!"
        }
    }

    private fun loadDashboardStatistics() {
        val orders = orderRepository.getOrders()

        var totalPendapatanVal = 0
        var activeCount = 0
        var completedCount = 0

        for (order in orders) {
            val cleanBiayaStr = order.totalBiaya.replace("[^0-9]".toRegex(), "")
            val biayaVal = cleanBiayaStr.toIntOrNull() ?: 0

            if (order.statusPembayaran.equals("Lunas", ignoreCase = true)) {
                totalPendapatanVal += biayaVal
            }

            when (order.statusPengerjaan.lowercase()) {
                "selesai", "siap diambil" -> completedCount++
                else -> activeCount++
            }
        }

        val formatRupiah = NumberFormat.getCurrencyInstance(Locale.Builder().setLanguage("id").setRegion("ID").build())
        formatRupiah.maximumFractionDigits = 0
        val formattedPendapatan = formatRupiah.format(totalPendapatanVal)

        binding.tvTotalPendapatan.text = formattedPendapatan
        binding.tvAntreanAktif.text = "$activeCount Kendaraan"
        binding.tvCucianSelesai.text = "$completedCount Cucian"
    }

    private fun setupClickListeners() {
        binding.cardInputTransaksi.setOnClickListener {
            val intent = Intent(requireContext(), ThirdActivity::class.java)
            startActivity(intent)
        }

        binding.cardDaftarAntrean.setOnClickListener {
            (requireActivity() as? BaseActivity)?.selectOrdersTab()
        }

        binding.cardNotes.setOnClickListener {
            val intent = Intent(requireContext(), NotesActivity::class.java)
            startActivity(intent)
        }

        binding.cardTasks.setOnClickListener {
            val intent = Intent(requireContext(), TasksActivity::class.java)
            startActivity(intent)
        }

        binding.cardLayananTarif.setOnClickListener {
            showServicesDialog()
        }

        binding.cardProfilDeveloper.setOnClickListener {
            val intent = Intent(requireContext(), DeveloperProfileActivity::class.java)
            startActivity(intent)
        }

        binding.cardWebView.setOnClickListener {
            val intent = Intent(requireContext(), WebViewActivity::class.java)
            startActivity(intent)
        }

        binding.cardLogout.setOnClickListener {
            showLogoutDialog()
        }

        binding.btnLogout.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun showServicesDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_services, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val rvServices = dialogView.findViewById<RecyclerView>(R.id.rvServices)
        val btnClose = dialogView.findViewById<MaterialButton>(R.id.btnCloseDialog)

        val sampleServices = listOf(
            ServiceItem(
                id = "SRV-01",
                name = "Cuci Mobil Salju + Vacuum",
                vehicleType = "MOBIL",
                price = "Rp 50.000",
                description = "Pembersihan bodi luar dengan busa salju premium, kompresor angin & vacuum interior.",
                estimatedTime = "45 Menit"
            ),
            ServiceItem(
                id = "SRV-02",
                name = "Cuci Mobil Hidrolik Detailing",
                vehicleType = "MOBIL",
                price = "Rp 85.000",
                description = "Cuci kolong hidrolik, mesin, bodi luar salju & poles kilap permukaan.",
                estimatedTime = "60 Menit"
            ),
            ServiceItem(
                id = "SRV-03",
                name = "Cuci Motor Reguler",
                vehicleType = "MOTOR",
                price = "Rp 20.000",
                description = "Pembersihan bodi motor salju, pengeringan lap microfiber & semir ban.",
                estimatedTime = "25 Menit"
            ),
            ServiceItem(
                id = "SRV-04",
                name = "Cuci Motor Besar / Moge",
                vehicleType = "MOTOR",
                price = "Rp 35.000",
                description = "Cuci salju bodi & rantai, pengeringan kompresor angin & pelumas rantai.",
                estimatedTime = "35 Menit"
            ),
            ServiceItem(
                id = "SRV-05",
                name = "Cuci Karpet Tebal (per Pcs)",
                vehicleType = "KARPET",
                price = "Rp 35.000",
                description = "Pencucian sikat halus, pembilasan tekanan tinggi & treatment anti-bakteri.",
                estimatedTime = "24 Jam"
            )
        )

        val serviceAdapter = ServiceAdapter { _ ->
            val intent = Intent(requireContext(), ThirdActivity::class.java)
            startActivity(intent)
            dialog.dismiss()
        }

        rvServices.layoutManager = LinearLayoutManager(requireContext())
        rvServices.adapter = serviceAdapter
        serviceAdapter.submitList(sampleServices)

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi Logout")
            .setMessage("Apakah Anda yakin ingin keluar dari akun?")
            .setPositiveButton("Ya") { dialog, _ ->
                val sharedPref = requireActivity().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.remove("isLogin")
                editor.remove("username")
                editor.apply()

                dialog.dismiss()

                val intent = Intent(requireContext(), AuthActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}