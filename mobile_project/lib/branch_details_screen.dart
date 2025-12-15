import 'package:flutter/material.dart';

class BranchDetailsScreen extends StatelessWidget {
  const BranchDetailsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Branch Details'),
      ),
      body: const Center(
        child: Text('Details about the branch.'),
      ),
    );
  }
}
