package HireCraft.com.SpringBoot.enums;

public enum PermissionName {
        //Admin Mgmt
        VIEW_ALL_USERS,
        MANAGE_USERS,
        APPROVE_DOCUMENTS,
        VIEW_ALL_REVIEWS,

        //User Mgmt
        VIEW_USER_PROFILE,
        EDIT_USER_PROFILE,
        DELETE_USER_ACCOUNT,
        SEND_MESSAGE,
        RECEIVE_MESSAGE,
        VIEW_REVIEWS,

        //Provider Permissions
        ACCEPT_BOOKING_REQUEST,
        DECLINE_BOOKING_REQUEST,
        UPLOAD_CV,
        UPLOAD_PORTFOLIO_IMAGES,
        RECEIVE_PAYMENT,
        WITHDRAW_PAYMENT,
        VIEW_EARNING_HISTORY,

        //Client Permissions
        BOOK_SERVICE_PROVIDER,
        ADD_REVIEW,
        MAKE_PAYMENT,
        VIEW_PAYMENT_HISTORY,

        //System Settings
        MANAGE_SETTINGS,
    }
