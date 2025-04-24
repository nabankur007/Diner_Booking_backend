package com.diner.backend.controller;

import com.diner.backend.dto.RestaurantCard;
import com.diner.backend.dto.RestaurantDisplayDto;
import com.diner.backend.dto.RestaurentInfo;
import com.diner.backend.dto.SignupRestaurantRequest;
import com.diner.backend.entity.*;
import com.diner.backend.entity.restaurant.*;
import com.diner.backend.repository.*;
import com.diner.backend.security.jwt.JwtUtils;
import com.diner.backend.security.response.LoginResponse;
import com.diner.backend.security.response.MessageResponse;
import com.diner.backend.service.UsersService;
import com.diner.backend.service.serviceimpl.CloudinaryImageServiceImpl;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class RestaurantController {

    @Autowired
    UsersService userService; // Service for user-related operations

    @Autowired
    private UsersRepo userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RoleRepo roleRepository;

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private RestaurantRepo restaurantRepository;


    @Autowired
    private CartRepo cartRepository;

    @Autowired
    private CloudinaryImageServiceImpl cloudinaryImageService;

    @PostMapping("/public/restaurant/signup")
    @Transactional
    public ResponseEntity<?> registerRestaurant(
            @Valid @RequestPart SignupRestaurantRequest signupRestaurantRequest,
            @RequestPart(required = false) MultipartFile[] restaurantImages,
            @RequestPart(required = false) MultipartFile[] menuImages,
            @RequestPart(required = false) MultipartFile[] diningImages) {

        // Validate username and email
        if (userRepository.existsByUserName(signupRestaurantRequest.getUserName())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }
        if (userRepository.existsByEmail(signupRestaurantRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create and save user
        Users user = new Users(
                signupRestaurantRequest.getUserName(),
                signupRestaurantRequest.getEmail(),
                encoder.encode(signupRestaurantRequest.getPassword()),
                signupRestaurantRequest.getPhoneNumber()
        );
        user.setRole(getRoleFromRequest(signupRestaurantRequest.getRole()));
        userRepository.save(user);

        // Upload images to Cloudinary
        List<String> foodImageUrls = uploadImagesToCloudinary(restaurantImages);
        List<String> menuImageUrls = uploadImagesToCloudinary(menuImages);
        List<String> diningImageUrls = uploadImagesToCloudinary(diningImages);

        // Create and save restaurant
        Restaurants restaurant = new Restaurants();
        restaurant.setPassword(encoder.encode(signupRestaurantRequest.getPassword()));
        restaurant.setRestaurantName(signupRestaurantRequest.getRestaurantName());
        restaurant.setDescription(signupRestaurantRequest.getDescription());
        restaurant.setPhoneNumber(signupRestaurantRequest.getPhoneNumber());
        restaurant.setEmail(signupRestaurantRequest.getEmail());
        restaurant.setOpeningTime(signupRestaurantRequest.getOpeningTime());
        restaurant.setClosingTime(signupRestaurantRequest.getClosingTime());
        restaurant.setCuisines(signupRestaurantRequest.getCuisines());
        restaurant.setOpeningDays(signupRestaurantRequest.getOpeningDays());
        restaurant.setShopNoBuildingNo(signupRestaurantRequest.getShopNoBuildingNo());
        restaurant.setTower(signupRestaurantRequest.getTower());
        restaurant.setArea(signupRestaurantRequest.getArea());
        restaurant.setCity(signupRestaurantRequest.getCity());
        restaurant.setLandmark(signupRestaurantRequest.getLandmark());
        restaurant.setLatitude(signupRestaurantRequest.getLatitude());
        restaurant.setLongitude(signupRestaurantRequest.getLongitude());
        restaurant.setFoodImageUrls(foodImageUrls);
        restaurant.setMenuImageUrls(menuImageUrls);
        restaurant.setDiningImageUrls(diningImageUrls);

        // Create and save cart
        Cart cart = new Cart();
        cart.setRestaurantName(restaurant.getRestaurantName());
        cartRepository.save(cart);

        restaurant.setCart(cart); // Set the entire cart object for proper relationship

        // Update restaurant with all relationships
        restaurantRepository.save(restaurant);

        // Authenticate and return JWT
        try {
            Authentication authentication = authenticate(user.getUserName(), signupRestaurantRequest.getPassword());
            String jwtToken = generateJwtToken(authentication);
            List<String> roles = getRolesFromAuthentication(authentication);

            return ResponseEntity.ok(new LoginResponse(
                    authentication.getName(),
                    roles,
                    jwtToken
            ));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Authentication failed"));
        }
    }

    private List<String> uploadImagesToCloudinary(MultipartFile[] images) {
        List<String> imageUrls = new ArrayList<>();
        if (images != null && images.length > 0) {
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    try {
                        Map uploadResult = cloudinaryImageService.uploadImage(image);
                        imageUrls.add((String) uploadResult.get("secure_url"));
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to upload image to Cloudinary", e);
                    }
                }
            }
        }
        return imageUrls;
    }

    private Authentication authenticate(String username, String password) throws AuthenticationException {
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    private String generateJwtToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return jwtUtils.generateTokenFromUsername(userDetails);
    }

    private List<String> getRolesFromAuthentication(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
    }

    private Role getRoleFromRequest(Set<String> strRoles) {
        if (strRoles == null || strRoles.isEmpty()) {
            return roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Default role (ROLE_USER) is not found."));
        }

        String roleStr = strRoles.iterator().next().toUpperCase();
        return switch (roleStr) {
            case "ADMIN" -> roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: ROLE_ADMIN is not found."));
            case "RESTAURANT" -> roleRepository.findByRoleName(AppRole.ROLE_RESTAURANT)
                    .orElseThrow(() -> new RuntimeException("Error: ROLE_RESTAURANT is not found."));
            default -> roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: ROLE_USER is not found."));
        };
    }

    @GetMapping("/public/getRestaurantData")
    public ResponseEntity<?> getRestaurantData(@AuthenticationPrincipal UserDetails userDetails) {
        // Get the email associated with the logged-in username
        Optional<String> emailOptional = usersRepo.getEmailFindByUserName(userDetails.getUsername());

        if (emailOptional.isEmpty()) {
//            System.out.println("Email not found for user: " + userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found for user");
        }

        String email = emailOptional.get();
//        System.out.println("Restaurant Email: " + email);

        // Find restaurant by email
        Optional<Restaurants> optionalRestaurant = restaurantRepository.findByEmail(email);

        if (optionalRestaurant.isEmpty()) {
//            System.out.println("Restaurant not found!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Restaurant not found");
        }

        Restaurants restaurant = optionalRestaurant.get();
//        System.out.println("Found restaurant: " + restaurant);

        // Create DTO
        RestaurentInfo restaurentInfo = new RestaurentInfo(
                restaurant.getRestaurantName(),
                restaurant.getDescription(),
                restaurant.getPhoneNumber(),
                restaurant.getEmail(),
                restaurant.getOpeningTime(),
                restaurant.getClosingTime(),
                restaurant.getMenuImageUrls(),
                restaurant.getFoodImageUrls(),
                restaurant.getDiningImageUrls(),
                restaurant.getCuisines(),
                restaurant.getOpeningDays(),
                restaurant.getShopNoBuildingNo(),
                restaurant.getTower(),
                restaurant.getArea(),
                restaurant.getCity(),
                restaurant.getLandmark(),
                restaurant.getLatitude(),
                restaurant.getLongitude()
        );

        return ResponseEntity.ok(restaurentInfo);
    }

    //card section
    @GetMapping("/public/restaurant/cards")
    public ResponseEntity<List<RestaurantCard>> getAllRestaurantCards() {
        List<Restaurants> restaurants = restaurantRepository.findAll();

        List<RestaurantCard> cards = restaurants.stream()
                .filter(Restaurants::getIsActive) // Only active restaurants
                .map(r -> new RestaurantCard(
                        r.getDiningImageUrls() != null && !r.getDiningImageUrls().isEmpty() ? r.getDiningImageUrls().get(0) : null,
                        r.getRestaurantName(),
                        r.getAverageRating(),
                        r.getCuisines().stream().map(Enum::name).collect(Collectors.toList()),
                        r.getOpeningTime(),
                        r.getCity()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(cards);
    }

    //individual card section
    @GetMapping("/public/restaurant/by-name")
    public ResponseEntity<?> getRestaurantByName(@RequestParam String name) {
        String cleanedName = name.trim(); // Removes leading/trailing whitespace & newline

        Optional<Restaurants> optionalRestaurant = restaurantRepository.findByRestaurantNameIgnoreCase(cleanedName);

        if (optionalRestaurant.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Restaurant not found");
        }
        Restaurants restaurant = optionalRestaurant.get();

        RestaurantDisplayDto dto = new RestaurantDisplayDto();
        dto.setRestaurantId(restaurant.getId());
        dto.setRestaurantName(restaurant.getRestaurantName());
        dto.setDescription(restaurant.getDescription());
        dto.setEmail(restaurant.getEmail());
        dto.setPhoneNumber(restaurant.getPhoneNumber());
        dto.setCity(restaurant.getCity());
        dto.setArea(restaurant.getArea());
        dto.setLandmark(restaurant.getLandmark());
        dto.setLatitude(restaurant.getLatitude());
        dto.setLongitude(restaurant.getLongitude());
        dto.setFoodImageUrls(restaurant.getFoodImageUrls());
        dto.setMenuImageUrls(restaurant.getMenuImageUrls());
        dto.setDiningImageUrls(restaurant.getDiningImageUrls());
        dto.setCuisines(restaurant.getCuisines());
        dto.setOpeningTime(restaurant.getOpeningTime());
        dto.setClosingTime(restaurant.getClosingTime());
        dto.setOpeningDays(restaurant.getOpeningDays());
        dto.setAverageRating(restaurant.getAverageRating());
        dto.setIsActive(restaurant.getIsActive());

        // Derived fields
        dto.setOperatingHours(restaurant.getOpeningTime() + " - " + restaurant.getClosingTime());
        dto.setShortDescription(restaurant.getDescription().length() > 100
                ? restaurant.getDescription().substring(0, 97) + "..."
                : restaurant.getDescription());

        return ResponseEntity.ok(dto);
    }


}