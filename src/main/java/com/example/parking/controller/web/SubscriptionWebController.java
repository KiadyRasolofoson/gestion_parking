package com.example.parking.controller.web;

import com.example.parking.dto.request.CreateSubscriptionRequest;
import com.example.parking.dto.response.SubscriptionResponse;
import com.example.parking.entity.enums.SubscriptionStatus;
import com.example.parking.entity.enums.SubscriptionType;
import com.example.parking.service.SubscriptionService;
import com.example.parking.service.UserService;
import com.example.parking.service.VehicleService;
import com.example.parking.util.WebQueryUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping("/subscriptions")
public class SubscriptionWebController {

    private final SubscriptionService subscriptionService;
    private final UserService userService;
    private final VehicleService vehicleService;

    public SubscriptionWebController(SubscriptionService subscriptionService,
                                     UserService userService,
                                     VehicleService vehicleService) {
        this.subscriptionService = subscriptionService;
        this.userService = userService;
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String search,
                       @RequestParam(required = false) SubscriptionStatus status,
                       @RequestParam(required = false) SubscriptionType type,
                       @RequestParam(required = false)
                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate validOn,
                       @PageableDefault(size = 15, sort = "startDate", direction = Sort.Direction.DESC)
                       Pageable pageable,
                       Model model) {
        Page<SubscriptionResponse> page = subscriptionService.search(search, status, type, null, validOn, pageable);

        Map<String, Object> filters = new LinkedHashMap<>();
        filters.put("search", search);
        filters.put("status", status);
        filters.put("type", type);
        filters.put("validOn", validOn);
        filters.put("sort", WebQueryUtils.sortParam(pageable));

        model.addAttribute("page", page);
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        model.addAttribute("type", type);
        model.addAttribute("validOn", validOn);
        model.addAttribute("filterQuery", WebQueryUtils.buildFilterQuery(filters));
        model.addAttribute("activeMenu", "subscriptions");
        return "subscriptions/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        CreateSubscriptionRequest form = new CreateSubscriptionRequest();
        form.setStartDate(LocalDate.now());
        form.setType(SubscriptionType.MENSUEL);
        model.addAttribute("form", form);
        populateFormData(model);
        return "subscriptions/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") CreateSubscriptionRequest form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateFormData(model);
            return "subscriptions/form";
        }
        SubscriptionResponse created = subscriptionService.create(form);
        redirectAttributes.addFlashAttribute("successMessage",
                "Abonnement " + created.reference() + " cree jusqu'au " + created.endDate());
        return "redirect:/subscriptions";
    }

    @PostMapping("/{id}/suspend")
    public String suspend(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        SubscriptionResponse updated = subscriptionService.suspend(id);
        redirectAttributes.addFlashAttribute("successMessage",
                "Abonnement " + updated.reference() + " suspendu");
        return "redirect:/subscriptions";
    }

    @PostMapping("/{id}/reactivate")
    public String reactivate(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        SubscriptionResponse updated = subscriptionService.reactivate(id);
        redirectAttributes.addFlashAttribute("successMessage",
                "Abonnement " + updated.reference() + " reactive");
        return "redirect:/subscriptions";
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        SubscriptionResponse updated = subscriptionService.cancel(id);
        redirectAttributes.addFlashAttribute("successMessage",
                "Abonnement " + updated.reference() + " annule");
        return "redirect:/subscriptions";
    }

    @PostMapping("/refresh-expired")
    public String refreshExpired(RedirectAttributes redirectAttributes) {
        int count = subscriptionService.refreshExpiredSubscriptions();
        redirectAttributes.addFlashAttribute("successMessage", count + " abonnement(s) passe(s) en EXPIRE");
        return "redirect:/subscriptions";
    }

    private void populateFormData(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("vehicles", vehicleService.findAll());
        Map<String, Object> prices = new LinkedHashMap<>();
        for (SubscriptionType type : SubscriptionType.values()) {
            prices.put(type.name(), subscriptionService.catalogPrice(type));
        }
        model.addAttribute("catalogPrices", prices);
        model.addAttribute("activeMenu", "subscriptions");
    }
}
