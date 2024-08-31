package com.example.securityOAuth.controller;

//@RestController
//@RequiredArgsConstructor
//public class UserController {
//
//    private final UserService userService;
//
//    @PostMapping("/user/{email}")
//    public void changeToAdmin(@PathVariable String email) {
//        userService.findByEmail(email).ifPresent(userEntity -> {
//            userEntity.setRole(UserRole.ROLE_ADMIN);
//            userService.save(userEntity);
//        });
//    }
//
//    @GetMapping("favicon.ico")
//    void favicon() {
//        // No operation. No favicon.
//    }
//
//    @GetMapping("/user/me")
//    public ResponseEntity<?> getCurrentUser(Authentication authentication, HttpServletRequest request) {
//        System.out.println("Headers: " + Collections.list(request.getHeaderNames())
//                .stream()
//                .collect(Collectors.toMap(h -> h, request::getHeader)));
//        if (authentication != null && authentication.isAuthenticated()) {
//            return ResponseEntity.ok(authentication.getName());  // Or fetch more details as needed
//        }
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//    }
//
//}
