import 'package:flutter/material.dart';
import 'branch_details_screen.dart';

class BranchesScreen extends StatelessWidget {
  const BranchesScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Golden Gym Branch'),
      ),
      body: Center(
        child: ListTile(
          leading: const Icon(Icons.store, size: 40),
          title: const Text('Main Branch', style: TextStyle(fontSize: 20)),
          subtitle: const Text('123 Main St, Anytown USA\nTel: 555-0101', style: TextStyle(fontSize: 16)),
          onTap: () {
            Navigator.push(
              context,
              MaterialPageRoute(builder: (context) => const BranchDetailsScreen()),
            );
          },
        ),
      ),
    );
  }
}
