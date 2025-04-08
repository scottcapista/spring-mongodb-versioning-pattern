const express = require('express');
const router = express.Router();
const memberService = require('../services/memberService');

// ...existing code...

/**
 * @route GET /api/members/:memberId
 * @description Get current record for a specific member ID
 * @access Public
 */
router.get('/:memberId', async (req, res) => {
  try {
    const memberId = req.params.memberId;
    const member = await memberService.getMemberById(memberId);
    
    if (!member) {
      return res.status(404).json({ message: 'Member not found' });
    }
    
    res.json(member);
  } catch (error) {
    console.error('Error fetching member:', error);
    res.status(500).json({ message: 'Server error' });
  }
});

// ...existing code...

module.exports = router;