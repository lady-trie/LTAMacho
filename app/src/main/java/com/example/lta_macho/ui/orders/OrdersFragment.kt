package com.example.lta_macho.ui.orders

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lta_macho.BaseActivity
import com.example.lta_macho.R
import com.example.lta_macho.databinding.FragmentOrdersBinding

class OrdersFragment : Fragment() {

    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!

    private lateinit var orderRepository: OrderRepository
    private lateinit var orderAdapter: OrderAdapter

    private var allOrders: List<Order> = emptyList()

    private var currentSearchQuery: String = ""
    private var selectedPengerjaanFilter: String = "Semua"
    private var selectedPembayaranFilter: String = "Semua"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as? BaseActivity)?.setToolbarTitle("Antrean Cucian")

        orderRepository = OrderRepository(requireContext())

        setupRecyclerView()
        setupSearchView()
        setupChipGroupFilters()

        binding.btnNewOrder.setOnClickListener {
            val intent = Intent(requireContext(), ThirdActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        allOrders = orderRepository.getOrders()
        applyFilters()
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter(
            onSendWaClick = { order ->
                val message = WhatsAppHelper.buildReceiptMessage(order)
                WhatsAppHelper.sendWhatsAppMessage(requireContext(), order.noWhatsapp, message)
            },
            onItemClick = { order ->
                val intent = Intent(requireContext(), ThirdResultActivity::class.java).apply {
                    putExtra("EXTRA_NAMA", order.namaPelanggan)
                    putExtra("EXTRA_PLAT", order.platNomor)
                    putExtra("EXTRA_NO_HP", order.noWhatsapp)
                    putExtra("EXTRA_LAYANAN", order.jenisLayanan)
                    putExtra("EXTRA_TOTAL", order.totalBiaya)
                    putExtra("EXTRA_STATUS_PENGERJAAN", order.statusPengerjaan)
                    putExtra("EXTRA_STATUS_PEMBAYARAN", order.statusPembayaran)
                }
                startActivity(intent)
            }
        )

        binding.rvOrders.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderAdapter
        }
    }

    private fun setupSearchView() {
        binding.searchViewOrders.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                currentSearchQuery = query.orEmpty().trim()
                applyFilters()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                currentSearchQuery = newText.orEmpty().trim()
                applyFilters()
                return true
            }
        })
    }

    private fun setupChipGroupFilters() {
        binding.chipGroupPengerjaan.setOnCheckedStateChangeListener { _, checkedIds ->
            selectedPengerjaanFilter = when (checkedIds.firstOrNull()) {
                R.id.chipPengerjaanProses -> "Proses"
                R.id.chipPengerjaanBelum -> "Belum Dicuci"
                R.id.chipPengerjaanSelesai -> "Selesai"
                else -> "Semua"
            }
            applyFilters()
        }

        binding.chipGroupPembayaran.setOnCheckedStateChangeListener { _, checkedIds ->
            selectedPembayaranFilter = when (checkedIds.firstOrNull()) {
                R.id.chipPembayaranLunas -> "Lunas"
                R.id.chipPembayaranBelum -> "Belum Bayar"
                else -> "Semua"
            }
            applyFilters()
        }
    }

    private fun applyFilters() {
        val filteredList = allOrders.filter { order ->
            val matchesQuery = if (currentSearchQuery.isEmpty()) {
                true
            } else {
                order.namaPelanggan.contains(currentSearchQuery, ignoreCase = true) ||
                        order.platNomor.contains(currentSearchQuery, ignoreCase = true) ||
                        order.noWhatsapp.contains(currentSearchQuery, ignoreCase = true)
            }

            val matchesPengerjaan = if (selectedPengerjaanFilter == "Semua") {
                true
            } else {
                order.statusPengerjaan.equals(selectedPengerjaanFilter, ignoreCase = true)
            }

            val matchesPembayaran = if (selectedPembayaranFilter == "Semua") {
                true
            } else {
                order.statusPembayaran.equals(selectedPembayaranFilter, ignoreCase = true)
            }

            matchesQuery && matchesPengerjaan && matchesPembayaran
        }

        orderAdapter.submitList(filteredList)

        if (filteredList.isEmpty()) {
            binding.tvEmptyState.visibility = View.VISIBLE
            binding.rvOrders.visibility = View.GONE
        } else {
            binding.tvEmptyState.visibility = View.GONE
            binding.rvOrders.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}