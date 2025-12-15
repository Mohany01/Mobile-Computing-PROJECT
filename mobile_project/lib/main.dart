import 'package:flutter/material.dart';

void main() {
  runApp(const GymBranchesApp());
}

class GymBranchesApp extends StatelessWidget {
  const GymBranchesApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Golden Gym Branches',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(
          seedColor: const Color(0xFFFFD700),
        ),
        useMaterial3: true,
      ),
      home: const GymBranchesScreen(),
    );
  }
}

class GymBranchesScreen extends StatelessWidget {
  const GymBranchesScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final branches = <GymBranch>[
      const GymBranch(
        name: 'Golden Gym - Nasr City',
        address: '12 Abbas El Akkad St, Nasr City, Cairo',
        phone: '+20 111 222 3333',
        workingHours: 'Daily 7:00 AM - 11:00 PM',
      ),
      const GymBranch(
        name: 'Golden Gym - Maadi',
        address: '25 Street 9, Maadi, Cairo',
        phone: '+20 122 333 4444',
        workingHours: 'Daily 6:00 AM - 12:00 AM',
      ),
      const GymBranch(
        name: 'Golden Gym - New Cairo',
        address: '90th Street, 5th Settlement, New Cairo',
        phone: '+20 100 555 6666',
        workingHours: 'Daily 7:00 AM - 11:00 PM',
      ),
    ];

    return Scaffold(
      appBar: AppBar(
        title: const Text('Gym branches'),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () {
            // Back to Android when user presses back
            Navigator.of(context).maybePop();
          },
        ),
      ),
      body: ListView.separated(
        padding: const EdgeInsets.all(16),
        itemCount: branches.length,
        separatorBuilder: (_, __) => const SizedBox(height: 12),
        itemBuilder: (context, index) {
          final b = branches[index];
          return Card(
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(12),
            ),
            elevation: 2,
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    b.name,
                    style: Theme.of(context).textTheme.titleMedium,
                  ),
                  const SizedBox(height: 8),
                  Row(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Icon(Icons.location_on_outlined, size: 18),
                      const SizedBox(width: 6),
                      Expanded(
                        child: Text(
                          b.address,
                          style: Theme.of(context).textTheme.bodyMedium,
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 4),
                  Row(
                    children: [
                      const Icon(Icons.phone, size: 18),
                      const SizedBox(width: 6),
                      Text(
                        b.phone,
                        style: Theme.of(context).textTheme.bodyMedium,
                      ),
                    ],
                  ),
                  const SizedBox(height: 4),
                  Row(
                    children: [
                      const Icon(Icons.access_time, size: 18),
                      const SizedBox(width: 6),
                      Text(
                        b.workingHours,
                        style: Theme.of(context).textTheme.bodyMedium,
                      ),
                    ],
                  ),
                ],
              ),
            ),
          );
        },
      ),
    );
  }
}

class GymBranch {
  final String name;
  final String address;
  final String phone;
  final String workingHours;

  const GymBranch({
    required this.name,
    required this.address,
    required this.phone,
    required this.workingHours,
  });
}
