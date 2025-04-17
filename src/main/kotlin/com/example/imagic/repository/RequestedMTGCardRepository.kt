package com.example.imagic.repository

import com.example.imagic.model.db.RequestedMTGCardTableRow
import org.springframework.data.jpa.repository.JpaRepository

interface RequestedMTGCardRepository : JpaRepository<RequestedMTGCardTableRow, String>
