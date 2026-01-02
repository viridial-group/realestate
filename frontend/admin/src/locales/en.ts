export default {
  common: {
    appName: 'Real Estate Admin',
    welcome: 'Welcome',
    loading: 'Loading...',
    save: 'Save',
    cancel: 'Cancel',
    delete: 'Delete',
    edit: 'Edit',
    create: 'Create',
    search: 'Search',
    filter: 'Filter',
    reset: 'Reset',
    export: 'Export',
    import: 'Import',
    actions: 'Actions',
    status: 'Status',
    type: 'Type',
    date: 'Date',
    total: 'Total',
    view: 'View',
    details: 'Details',
    close: 'Close',
    confirm: 'Confirm',
    yes: 'Yes',
    no: 'No',
    back: 'Back',
    next: 'Next',
    previous: 'Previous',
    submit: 'Submit',
    clear: 'Clear',
    select: 'Select',
    all: 'All',
    none: 'None',
    refresh: 'Refresh',
    update: 'Update',
    createdAt: 'Created at',
    updatedAt: 'Updated at'
  },
  auth: {
    login: 'Sign In',
    logout: 'Sign Out',
    signup: 'Sign Up',
    email: 'Email',
    password: 'Password',
    forgotPassword: 'Forgot Password?',
    rememberMe: 'Remember me',
    signInWithGoogle: 'Sign in with Google',
    signInWithApple: 'Sign in with Apple',
    notMember: 'Not a Member yet?',
    createAccount: 'Create an Account',
    resetPassword: 'Reset Password',
    sendResetLink: 'Send Reset Link',
    backToLogin: 'Back to Login'
  },
  dashboard: {
    title: 'Administrator Dashboard',
    overview: 'Platform Overview',
    totalUsers: 'Total Users',
    activeUsers: 'Active Users',
    properties: 'Properties',
    organizations: 'Organizations',
    usersByRole: 'Users by Role',
    usersByStatus: 'Users by Status',
    quickActions: 'Quick Actions',
    manageUsers: 'Manage Users',
    manageProperties: 'Manage Properties',
    manageOrganizations: 'Manage Organizations',
    recentActivity: 'Recent Activity',
    thisMonth: 'this month'
  },
  users: {
    title: 'User Management',
    description: 'Manage all platform users',
    newUser: 'New User',
    editUser: 'Edit User',
    name: 'Name',
    firstName: 'First Name',
    lastName: 'Last Name',
    phone: 'Phone',
    rolesLabel: 'Roles',
    active: 'Active',
    inactive: 'Inactive',
    suspended: 'Suspended',
    pending: 'Pending',
    deleted: 'Deleted',
    activate: 'Activate',
    deactivate: 'Deactivate',
    suspend: 'Suspend',
    resetPassword: 'Reset Password',
    userDetails: 'User Details',
    userStats: 'User Statistics',
    roles: {
      admin: 'Admin',
      agent: 'Agent',
      freelance: 'Freelance',
      autoEntrepreneur: 'Auto-entrepreneur',
      particular: 'Particular',
      user: 'User',
      superAdmin: 'Super Admin'
    }
  },
  organizations: {
    title: 'Organization Management',
    description: 'Manage all platform organizations',
    newOrganization: 'New Organization',
    editOrganization: 'Edit Organization',
    organizationName: 'Organization Name',
    organizationType: 'Organization Type',
    agency: 'Agency',
    freelance: 'Freelance',
    company: 'Company',
    siret: 'SIRET',
    address: 'Address',
    userCount: 'Users',
    createdOn: 'Created on',
    activeOrganizations: 'Active Organizations',
    inactiveOrganizations: 'Inactive Organizations',
    suspendedOrganizations: 'Suspended Organizations'
  },
  properties: {
    title: 'Property Management',
    description: 'Manage all real estate properties',
    newProperty: 'New Property',
    editProperty: 'Edit Property',
    propertyTitle: 'Property Title',
    propertyType: 'Property Type',
    price: 'Price',
    address: 'Address',
    city: 'City',
    country: 'Country',
    bedrooms: 'Bedrooms',
    bathrooms: 'Bathrooms',
    area: 'Area',
    available: 'Available',
    sold: 'Sold',
    rented: 'Rented',
    viewMode: 'View Mode',
    grid: 'Grid',
    list: 'List',
    map: 'Map',
    sortBy: 'Sort By',
    minPrice: 'Min Price',
    maxPrice: 'Max Price',
    status: {
      available: 'Available',
      sold: 'Sold',
      rented: 'Rented',
      pending: 'Pending',
      draft: 'Draft',
      published: 'Published',
      archived: 'Archived'
    },
    mapView: {
      legend: 'Legend',
      propertiesOnMap: 'properties on map',
      propertiesFound: 'properties found',
      clickToView: 'Click to view details'
    }
  },
  billing: {
    title: 'Billing',
    description: 'Manage subscriptions and invoices',
    subscriptions: 'Subscriptions',
    activeSubscriptions: 'Active Subscriptions',
    pendingPayments: 'Pending Payments',
    monthlyRevenue: 'Monthly Revenue',
    totalRevenue: 'Total Revenue',
    plan: 'Plan',
    amount: 'Amount',
    startDate: 'Start Date',
    endDate: 'End Date',
    invoices: 'Invoices',
    cancel: 'Cancel Subscription',
    subscriptionDetails: 'Subscription Details',
    subscriptionFeatures: 'Subscription Features',
    includedFeatures: 'Included Features',
    limitsAndQuotas: 'Limits and Quotas',
    noFeaturesSpecified: 'No features specified for this plan',
    organization: 'Organization',
    organizationName: 'Name',
    organizationEmail: 'Email',
    recentInvoices: 'Recent Invoices',
    noInvoicesFound: 'No invoices found',
    allInvoices: 'All Invoices',
    activateRenewal: 'Activate Renewal',
    deactivateRenewal: 'Deactivate Renewal',
    renewSubscription: 'Renew Subscription',
    systemInformation: 'System Information',
    subscriptionNotFound: 'Subscription not found',
    viewInvoices: 'View Invoices',
    autoRenewal: 'Automatic Renewal',
    activated: 'Activated',
    deactivated: 'Deactivated',
    cancelledAt: 'Cancellation Date',
    trialEndDate: 'Trial End Date',
    features: {
      property_listing: 'Property Listings',
      basic_search: 'Basic Search',
      advanced_search: 'Advanced Search',
      property_details: 'Property Details',
      photo_upload: 'Photo Upload',
      virtual_tours: 'Virtual Tours',
      email_notifications: 'Email Notifications',
      basic_analytics: 'Basic Analytics',
      advanced_analytics: 'Advanced Analytics',
      mobile_app: 'Mobile App',
      crm_integration: 'CRM Integration',
      lead_management: 'Lead Management',
      document_management: 'Document Management',
      team_collaboration: 'Team Collaboration',
      custom_branding: 'Custom Branding',
      priority_support: 'Priority Support',
      api_access: 'API Access',
      white_label: 'White Label',
      multi_location: 'Multi-Location',
      workflow_automation: 'Workflow Automation',
      custom_reports: 'Custom Reports',
      dedicated_account_manager: 'Dedicated Account Manager'
    }
  },
  plans: {
    title: 'Subscription Plans',
    description: 'Manage subscription plans and their features',
    createPlan: 'Create Plan',
    editPlan: 'Edit Plan',
    createPlanDescription: 'Create a new subscription plan',
    editPlanDescription: 'Edit plan information',
    activePlans: 'Active Plans',
    inactivePlans: 'Inactive Plans',
    totalPlans: 'Total Plans',
    defaultPlans: 'Default Plans',
    plans: 'Plans',
    plansList: 'Plans List',
    noPlansFound: 'No plans found',
    searchPlaceholder: 'Search for a plan...',
    name: 'Name',
    namePlaceholder: 'E.g: Basic Plan, Premium Plan',
    descriptionLabel: 'Description',
    descriptionPlaceholder: 'Plan description',
    price: 'Price',
    pricePlaceholder: '0.00',
    currency: 'Currency',
    billingPeriod: 'Billing Period',
    monthly: 'Monthly',
    yearly: 'Yearly',
    active: 'Active',
    inactive: 'Inactive',
    default: 'Default',
    setAsDefault: 'Set as default plan',
    quotas: 'Quotas',
    quotasDescription: 'Plan limits and quotas',
    maxProperties: 'Max Properties',
    maxPropertiesHint: 'Set -1 for unlimited',
    maxUsers: 'Max Users',
    maxUsersHint: 'Set -1 for unlimited',
    maxStorage: 'Max Storage',
    maxStorageHint: 'Set -1 for unlimited',
    unlimited: 'Unlimited',
    unlimitedPlaceholder: '-1 for unlimited',
    featuresLabel: 'Features',
    featuresDescription: 'Features included in this plan',
    noFeatures: 'No features defined',
    more: 'more',
    addFeature: 'Add Feature',
    featurePlaceholder: 'Feature name',
    jsonPreview: 'JSON Preview',
    planInformation: 'Plan Information',
    statistics: 'Statistics',
    totalSubscriptions: 'Total subscriptions',
    confirmDelete: 'Are you sure you want to delete this plan?',
    created: 'Plan created successfully',
    updated: 'Plan updated successfully',
    deleted: 'Plan deleted successfully',
    loading: 'Loading...',
    notFound: 'Plan not found',
    errors: {
      loadFailed: 'Error loading plans',
      saveFailed: 'Error saving plan',
      deleteFailed: 'Error deleting plan'
    },
    features: {
      property_listing: 'Property Listings',
      basic_search: 'Basic Search',
      advanced_search: 'Advanced Search',
      property_details: 'Property Details',
      photo_upload: 'Photo Upload',
      virtual_tours: 'Virtual Tours',
      email_notifications: 'Email Notifications',
      basic_analytics: 'Basic Analytics',
      advanced_analytics: 'Advanced Analytics',
      mobile_app: 'Mobile App',
      crm_integration: 'CRM Integration',
      lead_management: 'Lead Management',
      document_management: 'Document Management',
      team_collaboration: 'Team Collaboration',
      custom_branding: 'Custom Branding',
      priority_support: 'Priority Support',
      api_access: 'API Access',
      white_label: 'White Label',
      multi_location: 'Multi-Location',
      workflow_automation: 'Workflow Automation',
      custom_reports: 'Custom Reports',
      dedicated_account_manager: 'Dedicated Account Manager',
      property_management: 'Property Management',
      user_management: 'User Management',
      organization_management: 'Organization Management',
      analytics: 'Analytics',
      reports: 'Reports'
    }
  },
  audit: {
    title: 'Audit & Logs',
    description: 'System actions and events history',
    eventsToday: 'Events Today',
    thisWeek: 'This Week',
    errorsLabel: 'Errors',
    action: 'Action',
    entity: 'Entity',
    level: 'Level',
    message: 'Message',
    timestamp: 'Timestamp',
    user: 'User',
    system: 'System',
    info: 'Info',
    warning: 'Warning',
    error: 'Error',
    critical: 'Critical'
  },
  notifications: {
    title: 'Notifications',
    description: 'Manage system and user notifications',
    unread: 'Unread',
    read: 'Read',
    markAsRead: 'Mark as Read',
    markAllAsRead: 'Mark All as Read',
    sendNotification: 'Send Notification',
    today: 'Today',
    thisWeek: 'This Week',
    system: 'System',
    user: 'User',
    alert: 'Alert',
    info: 'Information',
    new: 'New'
  },
  workflows: {
    title: 'Workflows',
    description: 'Manage approval workflows and business processes',
    createWorkflow: 'Create Workflow',
    editWorkflow: 'Edit Workflow',
    createWorkflowDescription: 'Create a new approval workflow',
    editWorkflowDescription: 'Edit workflow information',
    activeWorkflows: 'Active Workflows',
    inactiveWorkflows: 'Inactive Workflows',
    totalWorkflows: 'Total Workflows',
    pendingTasks: 'Pending Tasks',
    workflows: 'Workflows',
    tasks: 'Tasks',
    active: 'Active',
    inactive: 'Inactive',
    default: 'Default',
    action: 'Action',
    targetType: 'Target Type',
    status: 'Status',
    name: 'Name',
    namePlaceholder: 'Workflow name',
    descriptionLabel: 'Description',
    descriptionPlaceholder: 'Workflow description',
    selectAction: 'Select an action',
    targetTypePlaceholder: 'Property, Document, etc.',
    isDefault: 'Default workflow',
    setAsDefault: 'Set as default workflow',
    steps: 'Steps',
    stepsDescription: 'Workflow steps configuration',
    stepsPlaceholder: '[{"step": 1, "name": "Step 1", "role": "ADMIN"}]',
    stepsHint: 'JSON format: array of steps with step, name, role',
    stepName: 'Step Name',
    stepNamePlaceholder: 'E.g: Initial Review',
    stepRole: 'Required Role',
    stepRolePlaceholder: 'E.g: ADMIN, MANAGER',
    stepDescriptionPlaceholder: 'Step description (optional)',
    addStep: 'Add Step',
    requiredRoles: 'Required Roles',
    requiredRolesDescription: 'Roles required for this workflow',
    requiredRolesPlaceholder: '["ADMIN", "AGENT"]',
    requiredRolesHint: 'JSON format: array of roles',
    rolePlaceholder: 'Search or select a role',
    addRole: 'Add Role',
    jsonPreview: 'JSON Preview',
    noRoleFound: 'No role found',
    searchPlaceholder: 'Search workflow...',
    noWorkflowsFound: 'No workflows found',
    confirmDelete: 'Are you sure you want to delete this workflow?',
    created: 'Workflow created successfully',
    updated: 'Workflow updated successfully',
    deleted: 'Workflow deleted successfully',
    errors: {
      loadFailed: 'Failed to load workflows',
      saveFailed: 'Failed to save workflow',
      deleteFailed: 'Failed to delete workflow',
      loadWorkflowFailed: 'Failed to load workflow',
      startFailed: 'Failed to start workflow',
      taskActionFailed: 'Failed to perform task action'
    },
    actions: {
      propertyCreate: 'Property Creation',
      propertyUpdate: 'Property Update',
      propertyDelete: 'Property Delete',
      documentUpload: 'Document Upload',
      userCreate: 'User Creation'
    },
    workflowInformation: 'Workflow Information',
    statistics: 'Statistics',
    totalTasks: 'Total Tasks',
    approvedTasks: 'Approved Tasks',
    rejectedTasks: 'Rejected Tasks',
    tasksTimeline: 'Tasks Timeline',
    tasksTimelineDescription: 'Track tasks and their progression',
    noTasks: 'No tasks for this workflow',
    step: 'Step',
    assignedRole: 'Assigned Role',
    dueDate: 'Due Date',
    completedAt: 'Completed At',
    comments: 'Comments',
    approve: 'Approve',
    reject: 'Reject',
    startWorkflow: 'Start Workflow',
    startWorkflowDescription: 'Start this workflow for a specific target',
    targetId: 'Target ID',
    targetIdPlaceholder: 'Enter target ID (e.g., property ID)',
    start: 'Start',
    approveTask: 'Approve Task',
    rejectTask: 'Reject Task',
    approveTaskDescription: 'Approve this task and proceed to next step',
    rejectTaskDescription: 'Reject this task and stop the workflow',
    commentsPlaceholder: 'Add a comment (optional)',
    started: 'Workflow started successfully',
    taskApproved: 'Task approved successfully',
    taskRejected: 'Task rejected successfully',
    notFound: 'Workflow not found',
    loading: 'Loading...',
    taskStatus: {
      pending: 'Pending',
      inProgress: 'In Progress',
      approved: 'Approved',
      rejected: 'Rejected',
      cancelled: 'Cancelled'
    }
  },
  validation: {
    required: 'This field is required',
    email: 'Invalid email format',
    minLength: 'Minimum {min} characters required',
    maxLength: 'Maximum {max} characters allowed',
    passwordMismatch: 'Passwords do not match',
    invalidFormat: 'Invalid format',
    invalidUrl: 'The URL is not valid',
    invalidPhone: 'The phone number is not valid',
    invalidDomain: 'The domain is not valid',
    minValue: 'The value must be greater than or equal to {min}',
    maxValue: 'The value must be less than or equal to {max}',
    positive: 'The value must be positive',
    integer: 'The value must be an integer',
    atLeastOneRole: 'At least one role must be selected'
  },
  messages: {
    success: {
      created: 'Created successfully',
      updated: 'Updated successfully',
      deleted: 'Deleted successfully',
      saved: 'Saved successfully',
      activated: 'Activated successfully',
      suspended: 'Suspended successfully'
    },
    error: {
      generic: 'An error occurred',
      notFound: 'Resource not found',
      unauthorized: 'Unauthorized access',
      forbidden: 'Access forbidden',
      network: 'Network error',
      server: 'Server error'
    },
    confirm: {
      delete: 'Are you sure you want to delete this item?',
      deleteMultiple: 'Are you sure you want to delete {count} items?',
      cancel: 'Are you sure you want to cancel?',
      logout: 'Are you sure you want to logout?'
    }
  },
  documentation: {
    title: 'Documentation',
    description: 'Complete guide to using the Real Estate Admin platform',
    categoriesLabel: 'Categories',
    modulesLabel: 'Modules',
    quickStart: {
      title: 'Quick Start',
      description: 'Learn the basics to get started quickly'
    },
    features: {
      title: 'Main Features',
      properties: 'Property Management',
      propertiesDescription: 'Create, edit and manage your real estate properties',
      users: 'User Management',
      usersDescription: 'Administer users and their permissions',
      workflows: 'Approval Workflows',
      workflowsDescription: 'Configure custom approval processes',
      map: 'Interactive Map',
      mapDescription: 'Visualize your properties on an interactive map'
    },
    categories: {
      gettingStarted: 'Getting Started',
      faq: 'FAQ'
    },
    modules: {
      properties: 'Properties',
      propertiesDescription: 'Complete real estate property management',
      users: 'Users',
      usersDescription: 'User and role administration',
      organizations: 'Organizations',
      organizationsDescription: 'Multi-tenant organization management',
      workflows: 'Workflows',
      workflowsDescription: 'Configurable approval processes',
      billing: 'Billing',
      billingDescription: 'Subscription and invoice management',
      notifications: 'Notifications',
      notificationsDescription: 'Real-time notification system',
      documents: 'Documents',
      documentsDescription: 'Document management and storage',
      map: 'Map',
      mapDescription: 'Map visualization of properties'
    },
    gettingStarted: {
      title: 'Getting Started',
      description: 'Quick start guide to using the platform',
      welcome: 'Welcome',
      intro: 'Welcome to the Real Estate Admin platform. This documentation will guide you through all available features.',
      feature1: 'Complete real estate property management',
      feature2: 'Multi-tenant system with organizations',
      feature3: 'Configurable approval workflows',
      feature4: 'Interactive map to visualize properties',
      firstSteps: {
        title: 'First Steps',
        step1: {
          title: 'Login',
          description: 'Log in with your administrator credentials:'
        },
        step2: {
          title: 'Explore the Dashboard',
          description: 'The dashboard gives you an overview of:',
          item1: 'Property statistics',
          item2: 'Recent activities',
          item3: 'Charts and metrics'
        },
        step3: {
          title: 'Create Your First Property',
          description: 'Start by creating a property to explore all features.'
        }
      },
      navigation: {
        title: 'Navigation',
        description: 'The sidebar allows you to access all modules:',
        dashboard: 'Overview of your activity',
        properties: 'Manage your properties',
        users: 'User administration',
        organizations: 'Organization management'
      },
      nextSteps: {
        title: 'Next Steps',
        description: 'Explore the following modules to learn more:'
      }
    },
    properties: {
      title: 'Property Management',
      description: 'Complete guide to managing your real estate properties',
      overview: {
        title: 'Overview',
        description: 'The Properties module allows you to manage all your real estate listings.',
        feature1: 'Create and edit properties',
        feature2: 'Upload multiple images',
        feature3: 'Advanced filters and search',
        feature4: 'Interactive map visualization'
      },
      creating: {
        title: 'Creating a Property',
        description: 'Step-by-step guide to creating a new property',
        step1: {
          title: 'Access the Form',
          description: 'To start creating a property:',
          item1: 'Go to the Properties module',
          item2: 'Click on the "New Property" button',
          item3: 'The creation form opens',
          item4: 'You are ready to fill in the property information'
        },
        step2: {
          title: 'Fill in Information',
          description: 'Fill in all form fields:',
          field1: {
            label: 'Title and Description',
            description: 'Give an attractive title and detailed description'
          },
          field2: {
            label: 'Location',
            description: 'Complete address, city, postal code and GPS coordinates'
          },
          field3: {
            label: 'Characteristics',
            description: 'Price, area, number of bedrooms, bathrooms, etc.'
          },
          field4: {
            label: 'Type and Status',
            description: 'Select the type (Apartment, House, etc.) and initial status'
          }
        },
        step3: {
          title: 'Add Images',
          description: 'Upload images to illustrate your property:',
          tip: {
            title: 'Tip: ',
            description: 'The first image will be used as the main image. You can upload multiple images.'
          }
        },
        step4: {
          title: 'Save',
          description: 'Click "Create" to save your property. It will be created with the selected status.'
        }
      },
      editing: {
        title: 'Editing a Property',
        description: 'To edit an existing property:',
        step1: 'Find the property in the list and click on it',
        step2: 'Click on the "Edit" button',
        step3: 'Modify the desired fields and save'
      },
      views: {
        title: 'Display Modes',
        grid: {
          title: 'Grid View',
          description: 'Display your properties as cards with images.',
          usage: 'Ideal for quick visual navigation. Click on a card to see details.'
        },
        list: {
          title: 'List View',
          description: 'Display your properties in a detailed table.',
          usage: 'Perfect for comparing multiple properties side by side with all information visible.'
        },
        map: {
          title: 'Map View',
          description: 'Visualize your properties on an interactive map with markers.',
          usage: 'Useful for seeing the geographical distribution of your properties. Click on a marker to see details.'
        }
      },
      filters: {
        title: 'Available Filters',
        description: 'Use filters to quickly find properties:',
        organization: 'By Organization',
        organizationDescription: 'Filter properties by organization',
        type: 'By Type',
        typeDescription: 'Apartment, House, Land, etc.',
        price: 'By Price',
        priceDescription: 'Filter by price range',
        status: 'By Status',
        statusDescription: 'Available, Sold, Rented, etc.'
      }
    },
    users: {
      title: 'User Management',
      description: 'Guide to managing users and their permissions',
      overview: {
        title: 'Overview',
        description: 'The Users module allows you to manage all platform users, their roles and permissions.'
      },
      creating: {
        title: 'Creating a User',
        description: 'Step-by-step guide to creating a new user',
        step1: {
          title: 'Access the Users Module',
          description: 'Go to the sidebar menu and click on "Users", then on "New User".'
        },
        step2: {
          title: 'Fill in the Form',
          description: 'Fill in the user information:',
          field1: {
            label: 'First and Last Name',
            description: 'User\'s full name'
          },
          field2: {
            label: 'Email',
            description: 'Unique email address (will serve as login identifier)'
          },
          field3: {
            label: 'Password',
            description: 'Secure password (minimum 8 characters)'
          },
          field4: {
            label: 'Roles',
            description: 'Select one or more roles to define permissions'
          }
        },
        step3: {
          title: 'Save',
          description: 'Click "Create" to save the new user.'
        }
      },
      roles: {
        title: 'Available Roles',
        admin: 'Full access to all features',
        manager: 'Property and team management',
        agent: 'Management of own properties',
        permissions: 'Permissions',
        adminPermissions: 'All modules, user management, organizations, billing, audit',
        managerPermissions: 'Property management, teams, workflows, documents',
        agentPermissions: 'Creation and management of own properties, document consultation'
      },
      managing: {
        title: 'Managing Users',
        editing: {
          title: 'Editing a User',
          description: 'To edit a user\'s information:',
          step1: 'Find the user in the list',
          step2: 'Click on "Edit"',
          step3: 'Modify the desired fields and save'
        },
        deleting: {
          title: 'Deleting a User',
          description: 'Deleting a user is permanent. Make sure the user no longer has properties or assigned tasks.'
        }
      }
    },
    organizations: {
      title: 'Organization Management',
      description: 'Guide to managing multi-tenant organizations',
      overview: {
        title: 'Overview',
        description: 'Organizations allow you to separate data by client in a multi-tenant system.',
        benefit1: {
          title: 'Data Isolation',
          description: 'Each organization has its own data, users and properties'
        },
        benefit2: {
          title: 'Centralized Management',
          description: 'Manage multiple clients from a single interface'
        }
      },
      creating: {
        title: 'Creating an Organization',
        description: 'Step-by-step guide to creating a new organization',
        step1: {
          title: 'Access the Organizations Module',
          description: 'Go to the sidebar menu and click on "Organizations", then on "New Organization".'
        },
        step2: {
          title: 'Fill in Information',
          description: 'Fill in the following fields:',
          field1: {
            label: 'Name',
            description: 'Organization name (e.g., "Paris Real Estate Agency")'
          },
          field2: {
            label: 'Description',
            description: 'Description of the organization and its activities'
          },
          field3: {
            label: 'Domain',
            description: 'Organization\'s web domain (optional)'
          },
          field4: {
            label: 'Parent Organization',
            description: 'Select a parent organization if this organization is a subsidiary (optional)'
          }
        },
        step3: {
          title: 'Save',
          description: 'Click "Create" to save the new organization. You can then add users to it.'
        }
      },
      managing: {
        title: 'Managing Organizations',
        users: {
          title: 'Adding Users',
          description: 'To add users to an organization:',
          step1: 'Go to the organization detail page',
          step2: 'Click on "Add User"',
          step3: 'Select the user and their role in the organization'
        },
        hierarchy: {
          title: 'Organization Hierarchy',
          description: 'You can create parent and subsidiary organizations to manage complex structures. Subsidiaries inherit certain configurations from the parent organization.'
        }
      }
    },
    workflows: {
      title: 'Approval Workflows',
      description: 'Guide to configuring and using workflows',
      overview: {
        title: 'Overview',
        description: 'Workflows allow you to define custom approval processes for your properties and documents.',
        benefit1: {
          title: 'Automation',
          description: 'Automate approval processes to save time'
        },
        benefit2: {
          title: 'Traceability',
          description: 'Track each approval step with complete history'
        },
        benefit3: {
          title: 'Flexibility',
          description: 'Configure workflows adapted to your business needs'
        },
        benefit4: {
          title: 'Security',
          description: 'Control who can approve what with required roles'
        }
      },
      creating: {
        title: 'Creating a Workflow',
        description: 'Step-by-step guide to creating an approval workflow',
        step1: {
          title: 'Access the Workflows Module',
          description: 'To start creating a workflow:',
          item1: 'Go to the sidebar menu and click on "Workflows"',
          item2: 'Click on the "Create Workflow" button',
          item3: 'The creation form opens'
        },
        step2: {
          title: 'Fill in Basic Information',
          description: 'Fill in the following fields:',
          field1: {
            label: 'Workflow Name',
            description: 'Give a clear and descriptive name (e.g., "Property Creation Approval")'
          },
          field2: {
            label: 'Description',
            description: 'Describe the purpose and context of the workflow'
          },
          field3: {
            label: 'Action',
            description: 'Select the trigger action (PROPERTY_CREATE, PROPERTY_UPDATE, etc.)'
          },
          field4: {
            label: 'Status',
            description: 'Activate or deactivate the workflow (only active workflows can be used)'
          }
        },
        step3: {
          title: 'Configure Approval Steps',
          description: 'Define the sequential steps of your workflow:',
          example: {
            title: 'Example of a 3-step workflow',
            step1: {
              name: 'Initial Validation',
              role: 'Role: AGENT'
            },
            step2: {
              name: 'Manager Review',
              role: 'Role: MANAGER'
            },
            step3: {
              name: 'Final Approval',
              role: 'Role: ADMIN'
            }
          },
          tip: {
            title: 'Tip: ',
            description: 'Steps are executed in order. Each step must be approved before moving to the next one.'
          }
        },
        step4: {
          title: 'Define Required Roles',
          description: 'Specify which roles can participate in the workflow:',
          item1: 'Click on "Add Role"',
          item2: 'Select a role from the dropdown',
          item3: 'Repeat for all necessary roles'
        },
        step5: {
          title: 'Save the Workflow',
          description: 'Click "Create" to save your workflow. You can mark it as "Default" so it is automatically used for the selected action.'
        }
      },
      using: {
        title: 'Using a Workflow',
        starting: {
          title: 'Starting a Workflow',
          description: 'To start a workflow on a resource:',
          step1: 'Go to the workflow detail page',
          step2: 'Click on "Start Workflow"',
          step3: 'Enter the target resource ID (property, document, etc.)'
        },
        tasks: {
          title: 'Managing Tasks',
          description: 'Tasks represent the steps to approve. Available statuses:',
          status: {
            pending: 'Awaiting assignment',
            inProgress: 'In progress',
            approved: 'Approved and validated',
            rejected: 'Rejected',
            cancelled: 'Cancelled'
          }
        }
      },
      bestPractices: {
        title: 'Best Practices',
        item1: {
          title: 'Name your workflows clearly',
          description: 'Use descriptive names that clearly indicate the purpose of the workflow.'
        },
        item2: {
          title: 'Limit the number of steps',
          description: 'A workflow with too many steps can slow down processes. 3-5 steps are generally optimal.'
        },
        item3: {
          title: 'Test before marking as default',
          description: 'Test your workflow on a few cases before marking it as the default workflow.'
        }
      }
    },
    billing: {
      title: 'Billing and Subscriptions',
      description: 'Guide to managing subscriptions and invoices',
      overview: {
        title: 'Overview',
        description: 'The Billing module manages subscriptions, pricing plans and invoices.',
        feature1: 'Subscription management per organization',
        feature2: 'Invoice and payment tracking',
        feature3: 'Pricing plans with quotas and features'
      },
      subscriptions: {
        title: 'Subscriptions',
        viewing: {
          title: 'Viewing Subscriptions',
          description: 'To see an organization\'s subscriptions:',
          step1: 'Go to the Billing module',
          step2: 'Select an organization from the list',
          step3: 'View the details of the active subscription'
        },
        details: {
          title: 'Subscription Details',
          description: 'The detail page displays:',
          info1: 'Pricing plan and included features',
          info2: 'Quotas used (properties, users, etc.)',
          info3: 'Start and end dates',
          info4: 'Subscription status (ACTIVE, EXPIRED, etc.)'
        }
      },
      invoices: {
        title: 'Invoices',
        viewing: {
          title: 'Viewing Invoices',
          description: 'To see invoices:',
          step1: 'Go to the Billing module',
          step2: 'Click on the "Invoices" tab',
          step3: 'Filter by organization or status if needed'
        },
        downloading: {
          title: 'Downloading an Invoice',
          description: 'Click the download button next to an invoice to download it as a PDF.'
        }
      }
    },
    notifications: {
      title: 'Notifications',
      description: 'Guide to using the notification system',
      overview: {
        title: 'Overview',
        description: 'The notification system allows you to receive and manage all your notifications in real time.',
        feature1: {
          title: 'Real-time Notifications',
          description: 'Receive instant notifications for important events'
        },
        feature2: {
          title: 'Centralized Management',
          description: 'View and manage all your notifications from one place'
        }
      },
      sending: {
        title: 'Sending a Notification',
        description: 'Guide to sending a notification to a user',
        step1: {
          title: 'Access the Notifications Module',
          description: 'Go to the sidebar menu and click on "Notifications", then on "Send Notification".'
        },
        step2: {
          title: 'Fill in the Form',
          description: 'Fill in the following information:',
          field1: {
            label: 'Recipient',
            description: 'Select the user who will receive the notification'
          },
          field2: {
            label: 'Title',
            description: 'Notification title (e.g., "New property to validate")'
          },
          field3: {
            label: 'Message',
            description: 'Detailed content of the notification'
          },
          field4: {
            label: 'Type',
            description: 'Notification type (INFO, WARNING, ERROR, SUCCESS)'
          }
        },
        step3: {
          title: 'Send',
          description: 'Click "Send" to send the notification. The recipient will receive it immediately.'
        }
      },
      managing: {
        title: 'Managing Notifications',
        reading: {
          title: 'Mark as Read',
          description: 'To mark a notification as read:',
          step1: 'Click on the notification in the list',
          step2: 'It will be automatically marked as read'
        },
        header: {
          title: 'Notifications in Header',
          description: 'The bell icon in the header displays the number of unread notifications. Click on it to see recent notifications.'
        }
      }
    },
    documents: {
      title: 'Document Management',
      description: 'Guide to uploading and managing documents',
      overview: {
        title: 'Overview',
        description: 'The Documents module allows you to upload, store and manage all your property-related documents.',
        feature1: 'Secure file upload (PDF, images, etc.)',
        feature2: 'Association with properties or resources',
        feature3: 'Download and preview'
      },
      uploading: {
        title: 'Uploading a Document',
        description: 'Step-by-step guide to uploading a document',
        step1: {
          title: 'Access the Documents Module',
          description: 'Go to the sidebar menu and click on "Documents", then on "Upload Document".'
        },
        step2: {
          title: 'Fill in the Form',
          description: 'Fill in the following information:',
          field1: {
            label: 'File',
            description: 'Select the file to upload (PDF, images, Word documents, etc.)'
          },
          field2: {
            label: 'Name',
            description: 'Give a descriptive name to the document'
          },
          field3: {
            label: 'Associated Property',
            description: 'Select the property this document is linked to (optional)'
          }
        },
        step3: {
          title: 'Upload',
          description: 'Click "Upload" to send the file. The document will be stored securely.',
          limit: {
            title: 'Limit: ',
            description: 'The maximum file size is 50 MB. Accepted formats: PDF, images (JPG, PNG), documents (DOC, DOCX).'
          }
        }
      },
      managing: {
        title: 'Managing Documents',
        viewing: {
          title: 'Viewing Documents',
          description: 'To see all documents:',
          step1: 'Go to the Documents module',
          step2: 'Filter by property or type if needed'
        },
        downloading: {
          title: 'Downloading a Document',
          description: 'Click the download button next to a document to download it to your computer.'
        },
        deleting: {
          title: 'Deleting a Document',
          description: 'Deleting a document is permanent. Make sure you no longer need it before deleting it.'
        }
      }
    },
    map: {
      title: 'Interactive Map',
      description: 'Guide to using the interactive map',
      overview: {
        title: 'Overview',
        description: 'The interactive map allows you to visualize all your properties on a map with markers colored according to their status.'
      },
      features: {
        markers: {
          title: 'Colored Markers',
          description: 'Each property is represented by a marker whose color indicates its status:'
        },
        clusters: {
          title: 'Automatic Clustering',
          description: 'Nearby properties are automatically grouped into clusters for better readability.'
        },
        popups: {
          title: 'Informative Popups',
          description: 'Click on a marker to see property details in a popup.'
        }
      }
    },
    faq: {
      title: 'Frequently Asked Questions',
      description: 'Find answers to the most common questions',
      items: {
        q1: 'How do I create a new property?',
        a1: 'Go to the Properties module, click on "New Property", fill in the form and save.',
        q2: 'How do I change my password?',
        a2: 'Go to your user profile and use the "Change Password" option.',
        q3: 'How do I filter properties?',
        a3: 'Use the filters at the top of the property list to filter by type, price, status, etc.',
        q4: 'How do I add images to a property?',
        a4: 'When creating or editing a property, use the "Images" section to upload multiple images.',
        q5: 'How do I configure an approval workflow?',
        a5: 'Go to the Workflows module, create a new workflow and configure approval steps with required roles.'
      }
    }
  }
}

