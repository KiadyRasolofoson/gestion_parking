package com.example.parking.controller.web;

import com.example.parking.dto.response.InvoiceResponse;
import com.example.parking.entity.enums.InvoiceStatus;
import com.example.parking.service.BillingService;
import com.example.parking.util.WebQueryUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping("/invoices")
public class InvoiceWebController {

    private final BillingService billingService;

    public InvoiceWebController(BillingService billingService) {
        this.billingService = billingService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String search,
                       @RequestParam(required = false) InvoiceStatus status,
                       @RequestParam(required = false)
                       @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime from,
                       @RequestParam(required = false)
                       @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime to,
                       @PageableDefault(size = 15, sort = "issuedAt", direction = Sort.Direction.DESC)
                       Pageable pageable,
                       Model model) {
        Page<InvoiceResponse> page = billingService.search(search, status, from, to, pageable);

        Map<String, Object> filters = new LinkedHashMap<>();
        filters.put("search", search);
        filters.put("status", status);
        filters.put("sort", WebQueryUtils.sortParam(pageable));

        model.addAttribute("page", page);
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        model.addAttribute("filterQuery", WebQueryUtils.buildFilterQuery(filters));
        model.addAttribute("activeMenu", "invoices");
        return "invoices/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("invoice", billingService.getById(id));
        model.addAttribute("activeMenu", "invoices");
        return "invoices/detail";
    }

    @PostMapping("/{id}/pay")
    public String pay(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        InvoiceResponse invoice = billingService.markAsPaid(id);
        redirectAttributes.addFlashAttribute("successMessage", "Facture " + invoice.reference() + " reglee");
        return "redirect:/invoices/" + id;
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        InvoiceResponse invoice = billingService.cancel(id);
        redirectAttributes.addFlashAttribute("successMessage", "Facture " + invoice.reference() + " annulee");
        return "redirect:/invoices/" + id;
    }
}
